package com.teamdelegation.engine;

import com.teamdelegation.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Balanced task assignment via weighted utility optimization.
 * Constraint: one task → one assignee. Members may be assigned multiple tasks (subject to workload cap).
 */
public class AssignmentEngine {

    private static final double THETA_MIN = 0.3;  // skill feasibility threshold
    private static final double BALANCE_THRESHOLD = 0.3;  // fairness correction trigger

    private final double nominalCapacityWeeks;
    private final Weights weights;

    public AssignmentEngine(double nominalCapacityWeeks, Weights weights) {
        this.nominalCapacityWeeks = nominalCapacityWeeks;
        this.weights = weights;
    }

    /**
     * Evaluate a single project (treated as one task) and assign to the best member.
     */
    public AssignmentDecision evaluate(ProjectDemand demand, List<Member> members) {
        Task task = demandToTask(demand);
        List<Task> tasks = List.of(task);
        List<TaskAssignment> assignments = assignTasks(tasks, members);

        TaskAssignment assignment = assignments.isEmpty() ? null : assignments.get(0);
        List<Member> recommendedTeam = assignment != null ? List.of(assignment.getAssignee()) : List.of();
        List<AssignmentInsight> insights = buildInsights(task, members);

        return new AssignmentDecision(demand, recommendedTeam, insights, assignments);
    }

    /**
     * Assign multiple tasks. Each task → one assignee; members may receive multiple tasks.
     */
    public List<TaskAssignment> assignTasks(List<Task> tasks, List<Member> members) {
        if (tasks.isEmpty() || members.isEmpty()) return List.of();

        // 1. Sort tasks by urgency (H→M→L), then duration (short→long)
        List<Task> sorted = tasks.stream()
                .sorted(Comparator
                        .comparing((Task t) -> -t.getUrgency().getWeight())
                        .thenComparingDouble(Task::getDurationWeeks))
                .toList();

        // Track cumulative load per member (copy to avoid mutating originals)
        Map<String, Double> loadByMember = new HashMap<>();
        for (Member m : members) {
            loadByMember.put(m.getName(), m.currentLoadRatio(nominalCapacityWeeks) * nominalCapacityWeeks);
        }

        List<TaskAssignment> assignments = new ArrayList<>();

        // 2. Greedy: for each task, pick argmax U_iℓ among feasible members
        for (Task task : sorted) {
            Member best = selectBestAssignee(task, members, loadByMember);
            if (best != null) {
                assignments.add(new TaskAssignment(task, best, computeUtility(task, best, loadByMember)));
                double currentLoad = loadByMember.get(best.getName());
                loadByMember.put(best.getName(), currentLoad + task.getDurationWeeks());
            }
        }

        // 3. Post-assignment balancing
        reassignForBalance(assignments, members, loadByMember);

        return assignments;
    }

    private Member selectBestAssignee(Task task, List<Member> members, Map<String, Double> loadByMember) {
        Member best = null;
        double bestUtility = Double.NEGATIVE_INFINITY;

        for (Member m : members) {
            if (!satisfiesWorkloadCap(m, task, loadByMember)) continue;
            double u = computeUtility(task, m, loadByMember);
            if (u > bestUtility) {
                bestUtility = u;
                best = m;
            }
        }
        return best;
    }

    private boolean satisfiesWorkloadCap(Member member, Task task, Map<String, Double> loadByMember) {
        double currentLoadWeeks = loadByMember.getOrDefault(member.getName(), 0.0);
        double newLoadWeeks = currentLoadWeeks + task.getDurationWeeks();
        return newLoadWeeks <= nominalCapacityWeeks;  // w_i + d_ℓ/C_i <= 1
    }

    /**
     * U_iℓ = α·(1-w_load) + β·e_i,s + γ·φ_i + δ·learning_bonus
     */
    private double computeUtility(Task task, Member member, Map<String, Double> loadByMember) {
        double wLoad = loadByMember.getOrDefault(member.getName(), 0.0) / nominalCapacityWeeks;
        double capacityScore = clamp(1 - wLoad);

        String primarySkill = task.getPrimarySkillDomain();
        double expertiseScore = member.getExpertise().getLevel(primarySkill);

        // Skill feasibility: if e_i,s < θ_min and !learning, heavily penalize
        if (expertiseScore < THETA_MIN && !task.isLearningOpportunity()) {
            return -10.0;  // heavily discouraged
        }

        double performanceScore = member.getRecentPerformance();

        // Learning bonus: if task offers learning and member not expert, add up to δ
        double skillFit = computeSkillFit(member, task);
        double learningBonus = 0.0;
        if (task.isLearningOpportunity() && skillFit < 1.0) {
            learningBonus = weights.learning * (1 - skillFit);
        }

        return weights.capacity * capacityScore
                + weights.skill * expertiseScore
                + weights.reliability * performanceScore
                + learningBonus;
    }

    private double computeSkillFit(Member member, Task task) {
        var required = task.getRequiredSkills().asMap();
        if (required.isEmpty()) return 0.5;
        double achieved = 0, total = 0;
        for (var e : required.entrySet()) {
            double demand = e.getValue();
            double memberLevel = member.getExpertise().getLevel(e.getKey());
            achieved += Math.min(demand, memberLevel);
            total += demand;
        }
        return total > 0 ? achieved / total : 0;
    }

    private void reassignForBalance(List<TaskAssignment> assignments, List<Member> members,
                                    Map<String, Double> loadByMember) {
        if (assignments.size() < 2) return;

        double maxLoad = loadByMember.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double minLoad = loadByMember.values().stream().mapToDouble(Double::doubleValue).min().orElse(0);
        if (maxLoad - minLoad <= BALANCE_THRESHOLD * nominalCapacityWeeks) return;

        // Find low-urgency task on most-loaded member, try to move to least-loaded (if skill feasible)
        Optional<TaskAssignment> toMove = assignments.stream()
                .filter(a -> a.getTask().getUrgency() == Urgency.L)
                .max(Comparator.comparingDouble(a -> loadByMember.getOrDefault(a.getAssignee().getName(), 0.0)));
        if (toMove.isEmpty()) return;

        Member from = toMove.get().getAssignee();
        Member to = members.stream()
                .min(Comparator.comparingDouble(m -> loadByMember.getOrDefault(m.getName(), Double.MAX_VALUE)))
                .orElse(null);
        if (to == null || to == from) return;

        double fromExpertise = to.getExpertise().getLevel(toMove.get().getTask().getPrimarySkillDomain());
        if (fromExpertise >= THETA_MIN || toMove.get().getTask().isLearningOpportunity()) {
            assignments.remove(toMove.get());
            double fromLoad = loadByMember.get(from.getName()) - toMove.get().getTask().getDurationWeeks();
            double toLoad = loadByMember.get(to.getName()) + toMove.get().getTask().getDurationWeeks();
            loadByMember.put(from.getName(), fromLoad);
            loadByMember.put(to.getName(), toLoad);
            assignments.add(new TaskAssignment(toMove.get().getTask(), to,
                    computeUtility(toMove.get().getTask(), to, loadByMember)));
        }
    }

    private Task demandToTask(ProjectDemand demand) {
        return new Task(
                "task-" + demand.getProjectName().hashCode(),
                demand.getProjectName(),
                demand.getDurationWeeks(),
                demand.getRequiredSkills(),
                demand.getUrgency(),
                demand.isLearningOpportunity(),
                demand.getObjectives()
        );
    }

    private List<AssignmentInsight> buildInsights(Task task, List<Member> members) {
        Map<String, Double> loadByMember = new HashMap<>();
        for (Member m : members) {
            loadByMember.put(m.getName(), m.currentLoadRatio(nominalCapacityWeeks) * nominalCapacityWeeks);
        }

        return members.stream()
                .map(m -> {
                    double wLoad = loadByMember.get(m.getName()) / nominalCapacityWeeks;
                    double capacityScore = clamp(1 - wLoad);
                    double expertiseScore = m.getExpertise().getLevel(task.getPrimarySkillDomain());
                    double skillFit = computeSkillFit(m, task);
                    double learningBonus = task.isLearningOpportunity() && skillFit < 1.0
                            ? weights.learning * (1 - skillFit) : 0.0;
                    double utility = weights.capacity * capacityScore + weights.skill * expertiseScore
                            + weights.reliability * m.getRecentPerformance() + learningBonus;
                    if (expertiseScore < THETA_MIN && !task.isLearningOpportunity()) utility = -10.0;
                    String narrative = String.format("capacity %.2f, expertise %.2f, skill fit %.2f, perf %.2f",
                            capacityScore, expertiseScore, skillFit, m.getRecentPerformance());
                    return new AssignmentInsight(m.getName(), utility, capacityScore, skillFit,
                            m.getRecentPerformance(), learningBonus / weights.learning, narrative);
                })
                .sorted(Comparator.comparingDouble(AssignmentInsight::getUtilityScore).reversed())
                .collect(Collectors.toList());
    }

    private double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }

    public static class Weights {
        public final double capacity;   // α
        public final double skill;      // β
        public final double reliability; // γ
        public final double learning;   // δ

        public Weights(double capacity, double skill, double reliability, double learning) {
            this.capacity = capacity;
            this.skill = skill;
            this.reliability = reliability;
            this.learning = learning;
        }

        /** Default: α=0.4, β=0.3, γ=0.2, δ=0.1 */
        public static Weights balanced() {
            return new Weights(0.4, 0.3, 0.2, 0.1);
        }

        /** Legacy: matches old 6-weight signature for backward compat */
        public static Weights balancedLegacy() {
            return new Weights(0.25, 0.25, 0.2, 0.1);
        }
    }
}
