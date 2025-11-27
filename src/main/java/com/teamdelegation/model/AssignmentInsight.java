package com.teamdelegation.model;

public class AssignmentInsight {
    private final String memberName;
    private final double utilityScore;
    private final double capacityScore;
    private final double skillFitScore;
    private final double reliabilityScore;
    private final double growthScore;
    private final String narrative;

    public AssignmentInsight(String memberName,
                             double utilityScore,
                             double capacityScore,
                             double skillFitScore,
                             double reliabilityScore,
                             double growthScore,
                             String narrative) {
        this.memberName = memberName;
        this.utilityScore = utilityScore;
        this.capacityScore = capacityScore;
        this.skillFitScore = skillFitScore;
        this.reliabilityScore = reliabilityScore;
        this.growthScore = growthScore;
        this.narrative = narrative;
    }

    public String getMemberName() {
        return memberName;
    }

    public double getUtilityScore() {
        return utilityScore;
    }

    public double getCapacityScore() {
        return capacityScore;
    }

    public double getSkillFitScore() {
        return skillFitScore;
    }

    public double getReliabilityScore() {
        return reliabilityScore;
    }

    public double getGrowthScore() {
        return growthScore;
    }

    public String getNarrative() {
        return narrative;
    }
}

