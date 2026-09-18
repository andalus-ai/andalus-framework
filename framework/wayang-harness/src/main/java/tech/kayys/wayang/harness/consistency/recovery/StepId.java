package tech.kayys.wayang.harness.consistency.recovery;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for an execution step.
 */
public record StepId(String value) {

    public StepId {
        Objects.requireNonNull(value, "StepId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("StepId value cannot be blank");
        }
    }

    public static StepId of(String value) {
        return new StepId(value);
    }

    public static StepId generate() {
        return new StepId("step-" + UUID.randomUUID());
    }
}
