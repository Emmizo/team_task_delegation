package com.teamdelegation.model;

import java.util.ArrayList;
import java.util.List;

public class Member {

    private final String name;
    private final SkillProfile expertise;
    private final double recentPerformance; // 0..1
    private final double growthDesire; // 0..1 willingness to stretch
    private final List<ProjectLoad> currentProjects = new ArrayList<>();

    public Member(String name,
                  SkillProfile expertise,
                  double recentPerformance,
                  double growthDesire) {
        this.name = name;
        this.expertise = expertise;
        this.recentPerformance = clamp(recentPerformance);
        this.growthDesire = clamp(growthDesire);
    }

    public String getName() {
        return name;
    }

    public SkillProfile getExpertise() {
        return expertise;
    }

    public double getRecentPerformance() {
        return recentPerformance;
    }

    public double getGrowthDesire() {
        return growthDesire;
    }

    public List<ProjectLoad> getCurrentProjects() {
        return currentProjects;
    }

    public Member assignProject(ProjectLoad load) {
        currentProjects.add(load);
        return this;
    }

    public double currentLoadRatio(double nominalCapacityWeeks) {
        double load = currentProjects.stream()
                .mapToDouble(ProjectLoad::getRemainingWeeks)
                .sum();
        return load / Math.max(1.0, nominalCapacityWeeks);
    }

    public double totalRemainingWeeks() {
        return currentProjects.stream()
                .mapToDouble(ProjectLoad::getRemainingWeeks)
                .sum();
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
}

