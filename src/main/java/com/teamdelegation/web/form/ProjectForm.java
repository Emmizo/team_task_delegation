package com.teamdelegation.web.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProjectForm {

    @NotBlank
    private String projectName;

    @NotNull
    @DecimalMin("1.0")
    private Double durationWeeks;

    /**
     * Format: each line "skill:intensity"
     */
    private String requiredSkillsRaw = "";

    /**
     * Format: comma-separated objectives.
     */
    private String objectivesRaw = "";

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Double getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(Double durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public String getRequiredSkillsRaw() {
        return requiredSkillsRaw;
    }

    public void setRequiredSkillsRaw(String requiredSkillsRaw) {
        this.requiredSkillsRaw = requiredSkillsRaw;
    }

    public String getObjectivesRaw() {
        return objectivesRaw;
    }

    public void setObjectivesRaw(String objectivesRaw) {
        this.objectivesRaw = objectivesRaw;
    }
}

