package com.teamdelegation.model;

import java.util.Set;

public class ProjectDemand {
    private final String projectName;
    private final SkillProfile requiredSkills;
    private final double durationWeeks;
    private final Set<String> objectives;

    public ProjectDemand(String projectName,
                         SkillProfile requiredSkills,
                         double durationWeeks,
                         Set<String> objectives) {
        this.projectName = projectName;
        this.requiredSkills = requiredSkills;
        this.durationWeeks = durationWeeks;
        this.objectives = objectives;
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
}

