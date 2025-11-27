package com.teamdelegation.web.util;

import com.teamdelegation.model.ProjectLoad;
import com.teamdelegation.model.SkillProfile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class TextParser {

    private TextParser() {
    }

    public static SkillProfile parseSkills(String raw) {
        Map<String, Double> skills = new HashMap<>();
        if (raw == null) {
            return SkillProfile.empty();
        }
        Arrays.stream(raw.split("\\r?\\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .forEach(line -> {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String skill = parts[0].trim().toLowerCase(Locale.ENGLISH);
                        double level = safeDouble(parts[1].trim(), 0.5);
                        skills.put(skill, level);
                    }
                });
        return new SkillProfile(skills);
    }

    public static Set<String> parseObjectives(String raw) {
        Set<String> objectives = new LinkedHashSet<>();
        if (raw == null || raw.isBlank()) {
            return objectives;
        }
        Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(objectives::add);
        return objectives;
    }

    public static void applyProjects(String raw, java.util.function.Consumer<ProjectLoad> consumer) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        Arrays.stream(raw.split("\\r?\\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .forEach(line -> {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String name = parts[0].trim();
                        double weeks = safeDouble(parts[1].trim(), 4.0);
                        consumer.accept(new ProjectLoad(name, weeks));
                    }
                });
    }

    private static double safeDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}

