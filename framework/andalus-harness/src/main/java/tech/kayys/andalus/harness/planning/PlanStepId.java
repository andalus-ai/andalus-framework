package tech.kayys.andalus.harness.planning;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a step within an agent plan.
 */
public record PlanStepId(String value) {
    public PlanStepId {
        Objects.requireNonNull(value, "PlanStepId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("PlanStepId value cannot be blank");
        }
    }

    public static PlanStepId of(String value) {
        return new PlanStepId(value);
    }

    public static PlanStepId generate() {
        return new PlanStepId("step-" + UUID.randomUUID());
    }
}
