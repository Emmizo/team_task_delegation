package com.teamdelegation.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents skill proficiency for a single member or task demand.
 */
public class SkillProfile {

    private final Map<String, Double> skills;

    public SkillProfile(Map<String, Double> skills) {
        this.skills = new HashMap<>();
        skills.forEach((k, v) -> this.skills.put(k.toLowerCase(), clamp(v)));
    }

    public static SkillProfile empty() {
        return new SkillProfile(Collections.emptyMap());
    }

    public double getLevel(String skill) {
        return skills.getOrDefault(skill.toLowerCase(), 0.0);
    }

    public Set<String> getSkillNames() {
        return skills.keySet();
    }

    public Map<String, Double> asMap() {
        return Collections.unmodifiableMap(skills);
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

