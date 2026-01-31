package com.teamdelegation.model;

import java.util.Set;

/**
 * A single task to assign. Each task is assigned to exactly one member.
 * Members may be assigned multiple tasks (subject to workload cap).
 */
public class Task {

    private final String id;
    private final String projectName;
    private final double durationWeeks;
    private final SkillProfile requiredSkills;
    private final Urgency urgency;
    private final boolean learningOpportunity;
    private final Set<String> objectives;

    public Task(String id,
                String projectName,
                double durationWeeks,
                SkillProfile requiredSkills,
                Urgency urgency,
                boolean learningOpportunity,
                Set<String> objectives) {
        this.id = id;
        this.projectName = projectName;
        this.durationWeeks = durationWeeks;
        this.requiredSkills = requiredSkills != null ? requiredSkills : SkillProfile.empty();
        this.urgency = urgency != null ? urgency : Urgency.M;
        this.learningOpportunity = learningOpportunity;
        this.objectives = objectives != null ? objectives : Set.of();
    }

    public String getId() {
        return id;
    }

    public String getProjectName() {
        return projectName;
    }

    public double getDurationWeeks() {
        return durationWeeks;
    }

    public SkillProfile getRequiredSkills() {
        return requiredSkills;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public boolean isLearningOpportunity() {
        return learningOpportunity;
    }

    public Set<String> getObjectives() {
        return objectives;
    }

    /** Primary skill domain (highest required level) for e_i,sℓ in utility. */
    public String getPrimarySkillDomain() {
        var map = requiredSkills.asMap();
        if (map.isEmpty()) return "general";
        return map.entrySet().stream()
                .max(java.util.Comparator.comparingDouble(java.util.Map.Entry::getValue))
                .map(java.util.Map.Entry::getKey)
                .orElse("general");
    }
}
