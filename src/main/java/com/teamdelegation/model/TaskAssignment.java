package com.teamdelegation.model;

/**
 * Result of assigning one task to one member.
 * One task → one assignee; a member may have multiple TaskAssignments.
 */
public class TaskAssignment {
    private final Task task;
    private final Member assignee;
    private final double utilityScore;

    public TaskAssignment(Task task, Member assignee, double utilityScore) {
        this.task = task;
        this.assignee = assignee;
        this.utilityScore = utilityScore;
    }

    public Task getTask() {
        return task;
    }

    public Member getAssignee() {
        return assignee;
    }

    public double getUtilityScore() {
        return utilityScore;
    }
}
