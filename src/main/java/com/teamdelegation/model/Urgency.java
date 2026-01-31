package com.teamdelegation.model;

/**
 * Task urgency level with corresponding weight for assignment ordering.
 * H → 1.0, M → 0.6, L → 0.3
 */
public enum Urgency {
    L(0.3),
    M(0.6),
    H(1.0);

    private final double weight;

    Urgency(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
