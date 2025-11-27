package com.teamdelegation.web.form;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MemberForm {

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private Double performance;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private Double growth;

    /**
     * Expected format: each line "skill:level"
     */
    private String skillsRaw = "";

    /**
     * Expected format: each line "project:weeks"
     */
    private String projectsRaw = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPerformance() {
        return performance;
    }

    public void setPerformance(Double performance) {
        this.performance = performance;
    }

    public Double getGrowth() {
        return growth;
    }

    public void setGrowth(Double growth) {
        this.growth = growth;
    }

    public String getSkillsRaw() {
        return skillsRaw;
    }

    public void setSkillsRaw(String skillsRaw) {
        this.skillsRaw = skillsRaw;
    }

    public String getProjectsRaw() {
        return projectsRaw;
    }

    public void setProjectsRaw(String projectsRaw) {
        this.projectsRaw = projectsRaw;
    }
}

