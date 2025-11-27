package com.teamdelegation.model;

import java.util.List;

public class AssignmentDecision {
    private final ProjectDemand demand;
    private final List<Member> recommendedTeam;
    private final List<AssignmentInsight> insights;

    public AssignmentDecision(ProjectDemand demand,
                              List<Member> recommendedTeam,
                              List<AssignmentInsight> insights) {
        this.demand = demand;
        this.recommendedTeam = recommendedTeam;
        this.insights = insights;
    }

    public ProjectDemand getDemand() {
        return demand;
    }

    public List<Member> getRecommendedTeam() {
        return recommendedTeam;
    }

    public List<AssignmentInsight> getInsights() {
        return insights;
    }
}

