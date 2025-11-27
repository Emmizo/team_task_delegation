package com.teamdelegation.web;

import com.teamdelegation.model.AssignmentDecision;
import com.teamdelegation.model.Member;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ScenarioRepository {

    private final List<Member> members = new ArrayList<>();
    private AssignmentDecision lastDecision;

    public List<Member> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public void replaceMembers(List<Member> seedMembers) {
        members.clear();
        members.addAll(seedMembers);
    }

    public AssignmentDecision getLastDecision() {
        return lastDecision;
    }

    public void setLastDecision(AssignmentDecision lastDecision) {
        this.lastDecision = lastDecision;
    }
}

