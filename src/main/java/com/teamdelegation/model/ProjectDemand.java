package com.teamdelegation.model;

import java.util.Set;

public class ProjectDemand {
    private final String projectName;
    private final SkillProfile requiredSkills;
    private final double durationWeeks;
    private final Set<String> objectives;
    private final Urgency urgency;
    private final boolean learningOpportunity;

    public ProjectDemand(String projectName,
                         SkillProfile requiredSkills,
                         double durationWeeks,
                         Set<String> objectives) {
        this(projectName, requiredSkills, durationWeeks, objectives, Urgency.M, false);
    }

    public ProjectDemand(String projectName,
                         SkillProfile requiredSkills,
                         double durationWeeks,
                         Set<String> objectives,
                         Urgency urgency,
                         boolean learningOpportunity) {
        this.projectName = projectName;
        this.requiredSkills = requiredSkills;
        this.durationWeeks = durationWeeks;
        this.objectives = objectives;
        this.urgency = urgency != null ? urgency : Urgency.M;
        this.learningOpportunity = learningOpportunity;
    }

    public String getProjectName() {
        return projectName;
    }

    public SkillProfile getRequiredSkills() {
        return requiredSkills;
    }

    public double getDurationWeeks() {
        return durationWeeks;
    }

    public Set<String> getObjectives() {
        return objectives;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public boolean isLearningOpportunity() {
        return learningOpportunity;
    }
}

