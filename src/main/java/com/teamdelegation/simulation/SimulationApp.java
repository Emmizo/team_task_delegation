package com.teamdelegation.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.teamdelegation.engine.AssignmentEngine;
import com.teamdelegation.model.AssignmentDecision;
import com.teamdelegation.model.AssignmentInsight;
import com.teamdelegation.model.Member;
import com.teamdelegation.model.ProjectDemand;
import com.teamdelegation.model.SkillProfile;

public class SimulationApp {

    private static final double DEFAULT_CAPACITY_WEEKS = 12.0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Member> members = new ArrayList<>(DemoDataFactory.buildMembers());
        System.out.println("=== Team Task Delegation Simulation ===");
        if (members.isEmpty()) {
            System.out.println("Starting with an empty roster. Add members when prompted.");
        } else {
            System.out.println("Loaded dataset with " + members.size() + " members.");
        }

        maybeAddMembers(scanner, members);

        ProjectDemand demand = collectProjectDemand(scanner);

        AssignmentEngine engine = new AssignmentEngine(
                DEFAULT_CAPACITY_WEEKS,
                AssignmentEngine.Weights.balanced()
        );

        AssignmentDecision decision = engine.evaluate(demand, members);

        presentDecision(decision);

        System.out.println("\nRun again with different data by re-launching the program. Goodbye!");
    }

    private static void maybeAddMembers(Scanner scanner, List<Member> members) {
        System.out.println("\nWould you like to add additional team members? (y/n)");
        String response = scanner.nextLine().trim().toLowerCase();
        while ("y".equals(response)) {
            members.add(collectMember(scanner));
            System.out.println("Member added. Add another? (y/n)");
            response = scanner.nextLine().trim().toLowerCase();
        }
    }

    private static Member collectMember(Scanner scanner) {
        System.out.println("Enter member name:");
        String name = scanner.nextLine().trim();

        System.out.println("Enter recent performance (0-1):");
        double performance = parseDouble(scanner.nextLine(), 0.75);

        System.out.println("Enter growth desire (0-1):");
        double growth = parseDouble(scanner.nextLine(), 0.6);

        Map<String, Double> skills = new HashMap<>();
        String addSkill = "y";
        while ("y".equals(addSkill)) {
            System.out.println("Skill name:");
            String skill = scanner.nextLine().trim().toLowerCase();
            System.out.println("Skill level (0-1):");
            double level = parseDouble(scanner.nextLine(), 0.5);
            skills.put(skill, level);
            System.out.println("Add another skill for this member? (y/n)");
            addSkill = scanner.nextLine().trim().toLowerCase();
        }

        Member member = new Member(name, new SkillProfile(skills), performance, growth);
        System.out.println("How many ongoing projects?");
        int count = parseInt(scanner.nextLine(), 0);
        for (int i = 0; i < count; i++) {
            System.out.println("Project " + (i + 1) + " name:");
            String project = scanner.nextLine().trim();
            System.out.println("Remaining weeks for " + project + ":");
            double weeks = parseDouble(scanner.nextLine(), 4.0);
            member.assignProject(new com.teamdelegation.model.ProjectLoad(project, weeks));
        }
        return member;
    }

    private static ProjectDemand collectProjectDemand(Scanner scanner) {
        System.out.println("\n--- New Project Demand ---");
        System.out.println("Project name:");
        String projectName = scanner.nextLine().trim();

        System.out.println("Duration in weeks:");
        double duration = parseDouble(scanner.nextLine(), 6.0);

        Map<String, Double> requiredSkills = new HashMap<>();
        String addSkill = "y";
        while ("y".equals(addSkill)) {
            System.out.println("Required skill name:");
            String skill = scanner.nextLine().trim().toLowerCase();
            System.out.println("Required intensity (0-1):");
            double intensity = parseDouble(scanner.nextLine(), 0.7);
            requiredSkills.put(skill, intensity);
            System.out.println("Add another required skill? (y/n)");
            addSkill = scanner.nextLine().trim().toLowerCase();
        }

        Set<String> objectives = new HashSet<>();
        System.out.println("List objectives or success criteria (type 'done' to finish):");
        while (true) {
            String line = scanner.nextLine().trim();
            if ("done".equalsIgnoreCase(line) || line.isBlank()) {
                break;
            }
            objectives.add(line);
        }

        return new ProjectDemand(projectName, new SkillProfile(requiredSkills), duration, objectives);
    }

    private static void presentDecision(AssignmentDecision decision) {
        System.out.println("\n=== Recommendation ===");
        System.out.println("Project: " + decision.getDemand().getProjectName());
        System.out.println("Suggested team:");
        decision.getRecommendedTeam().forEach(member ->
                System.out.println(" - " + member.getName()));

        System.out.println("\nDetailed insights (higher utility is better):");
        for (AssignmentInsight insight : decision.getInsights()) {
            System.out.printf(" * %s -> utility %.3f | capacity %.2f | skill %.2f | reliability %.2f | growth %.2f%n",
                    insight.getMemberName(),
                    insight.getUtilityScore(),
                    insight.getCapacityScore(),
                    insight.getSkillFitScore(),
                    insight.getReliabilityScore(),
                    insight.getGrowthScore());
            System.out.println("   reasoning: " + insight.getNarrative());
        }
    }

    private static double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}

