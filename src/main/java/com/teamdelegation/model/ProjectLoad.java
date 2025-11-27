package com.teamdelegation.model;

public class ProjectLoad {

    private final String projectName;
    private final double remainingWeeks;

    public ProjectLoad(String projectName, double remainingWeeks) {
        this.projectName = projectName;
        this.remainingWeeks = remainingWeeks;
    }

    public String getProjectName() {
        return projectName;
    }

    public double getRemainingWeeks() {
        return remainingWeeks;
    }
}

