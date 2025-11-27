package com.teamdelegation.engine;

import com.teamdelegation.model.AssignmentDecision;
import com.teamdelegation.model.AssignmentInsight;
import com.teamdelegation.model.Member;
import com.teamdelegation.model.ProjectDemand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class AssignmentEngine {

    private final double nominalCapacityWeeks;
    private final Weights weights;

    public AssignmentEngine(double nominalCapacityWeeks, Weights weights) {
        this.nominalCapacityWeeks = nominalCapacityWeeks;
        this.weights = weights;
    }

    public AssignmentDecision evaluate(ProjectDemand demand, List<Member> members) {
        List<AssignmentInsight> insights = members.stream()
                .map(member -> scoreMember(member, demand))
                .sorted(Comparator.comparingDouble(AssignmentInsight::getUtilityScore).reversed())
                .collect(Collectors.toList());

        List<Member> recommendedTeam = pickBalancedTeam(insights, members);

        return new AssignmentDecision(demand, recommendedTeam, insights);
    }

    private List<Member> pickBalancedTeam(List<AssignmentInsight> insights, List<Member> members) {
        int requiredSkillCount = (int) insights.stream()
                .findFirst()
                .map(AssignmentInsight::getSkillFitScore)
                .map(score -> Math.max(1, (int) Math.round(score * members.size() / 2.0)))
                .orElse(2);

        int nominalTeamSize = Math.min(members.size(), Math.max(2, requiredSkillCount));

        List<Member> selected = insights.stream()
                .limit(nominalTeamSize)
                .map(insight -> findMemberByName(insight.getMemberName(), members))
                .collect(Collectors.toCollection(ArrayList::new));

        boolean hasGrowth = selected.stream()
                .map(member -> findInsight(member.getName(), insights))
                .anyMatch(insight -> insight != null && insight.getGrowthScore() >= 0.25);

        if (!hasGrowth) {
            AssignmentInsight bestGrowth = insights.stream()
                    .filter(insight -> insight.getGrowthScore() >= 0.25)
                    .findFirst()
                    .orElse(null);
            if (bestGrowth != null) {
                Member growthMember = findMemberByName(bestGrowth.getMemberName(), members);
                if (!selected.contains(growthMember)) {
                    if (!selected.isEmpty()) {
                        selected.remove(selected.size() - 1);
                    }
                    selected.add(growthMember);
                }
            }
        }

        return selected;
    }

    private AssignmentInsight scoreMember(Member member, ProjectDemand demand) {
        double loadRatio = member.currentLoadRatio(nominalCapacityWeeks);
        double capacityScore = clamp(1 - (loadRatio));

        double skillFitScore = computeSkillFit(member, demand);
        double reliabilityScore = member.getRecentPerformance();
        double growthScore = computeGrowthScore(member, demand, skillFitScore);
        double objectiveAlignment = computeObjectiveAlignment(member, demand);
        double durationPenalty = clamp(demand.getDurationWeeks() / (2 * nominalCapacityWeeks));

        double utility = weights.capacity * capacityScore
                + weights.skill * skillFitScore
                + weights.reliability * reliabilityScore
                + weights.growth * growthScore
                + weights.objective * objectiveAlignment
                - weights.durationPenalty * durationPenalty;

        String narrative = buildNarrative(member, capacityScore, skillFitScore,
                reliabilityScore, growthScore, objectiveAlignment, durationPenalty);

        return new AssignmentInsight(member.getName(), utility, capacityScore,
                skillFitScore, reliabilityScore, growthScore, narrative);
    }

    private double computeSkillFit(Member member, ProjectDemand demand) {
        Map<String, Double> required = demand.getRequiredSkills().asMap();
        if (required.isEmpty()) {
            return 0;
        }
        double achieved = 0;
        double total = 0;
        for (Map.Entry<String, Double> entry : required.entrySet()) {
            double demandLevel = entry.getValue();
            double memberLevel = member.getExpertise().getLevel(entry.getKey());
            achieved += Math.min(demandLevel, memberLevel);
            total += demandLevel;
        }
        return achieved / total;
    }

    private double computeGrowthScore(Member member, ProjectDemand demand, double skillFit) {
        double stretchGap = Math.max(0, 1 - skillFit);
        double manageableGap = Math.min(1.0, stretchGap + 0.25);
        double loadFactor = 1 - member.currentLoadRatio(nominalCapacityWeeks);
        loadFactor = Math.max(0, loadFactor);
        return member.getGrowthDesire() * manageableGap * loadFactor;
    }

    private double computeObjectiveAlignment(Member member, ProjectDemand demand) {
        Set<String> objectives = demand.getObjectives();
        if (objectives == null || objectives.isEmpty()) {
            return 0.4; // neutral prior
        }
        String expertiseVector = member.getExpertise().getSkillNames().toString().toLowerCase(Locale.ENGLISH);
        long hits = objectives.stream()
                .map(obj -> obj.toLowerCase(Locale.ENGLISH))
                .filter(expertiseVector::contains)
                .count();
        return hits / (double) objectives.size();
    }

    private String buildNarrative(Member member,
                                  double capacityScore,
                                  double skillFitScore,
                                  double reliabilityScore,
                                  double growthScore,
                                  double objectiveAlignment,
                                  double durationPenalty) {
        StringJoiner joiner = new StringJoiner("; ");
        joiner.add(String.format("%s capacity %.2f (load penalty %.2f)", member.getName(), capacityScore, durationPenalty));
        joiner.add(String.format("skill fit %.2f, reliability %.2f", skillFitScore, reliabilityScore));
        joiner.add(String.format("growth %.2f, objectives %.2f", growthScore, objectiveAlignment));
        return joiner.toString();
    }

    private Member findMemberByName(String name, List<Member> members) {
        return members.stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown member " + name));
    }

    private AssignmentInsight findInsight(String name, List<AssignmentInsight> insights) {
        return insights.stream()
                .filter(i -> i.getMemberName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private double clamp(double value) {
        if (value < 0) {
            return 0;
        }
        if (value > 1) {
            return 1;
        }
        return value;
    }

    public static class Weights {
        public final double capacity;
        public final double skill;
        public final double reliability;
        public final double growth;
        public final double objective;
        public final double durationPenalty;

        public Weights(double capacity,
                       double skill,
                       double reliability,
                       double growth,
                       double objective,
                       double durationPenalty) {
            this.capacity = capacity;
            this.skill = skill;
            this.reliability = reliability;
            this.growth = growth;
            this.objective = objective;
            this.durationPenalty = durationPenalty;
        }

        public static Weights balanced() {
            return new Weights(0.25, 0.25, 0.2, 0.15, 0.1, 0.05);
        }
    }
}

