package com.teamdelegation.model;

import java.util.Collections;
import java.util.List;

public class AssignmentDecision {
    private final ProjectDemand demand;
    private final List<Member> recommendedTeam;
    private final List<AssignmentInsight> insights;
    private final List<TaskAssignment> taskAssignments;

    public AssignmentDecision(ProjectDemand demand,
                              List<Member> recommendedTeam,
                              List<AssignmentInsight> insights) {
        this(demand, recommendedTeam, insights, Collections.emptyList());
    }

    public AssignmentDecision(ProjectDemand demand,
                              List<Member> recommendedTeam,
                              List<AssignmentInsight> insights,
                              List<TaskAssignment> taskAssignments) {
        this.demand = demand;
        this.recommendedTeam = recommendedTeam;
        this.insights = insights;
        this.taskAssignments = taskAssignments != null ? taskAssignments : List.of();
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

    public List<TaskAssignment> getTaskAssignments() {
        return taskAssignments;
    }

    /** Primary assignee for single-task assignment. */
    public Member getAssignee() {
        if (taskAssignments.isEmpty()) {
            return recommendedTeam.isEmpty() ? null : recommendedTeam.get(0);
        }
        return taskAssignments.get(0).getAssignee();
    }
}

