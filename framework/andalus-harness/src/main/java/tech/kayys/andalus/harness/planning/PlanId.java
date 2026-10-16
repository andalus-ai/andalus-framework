package tech.kayys.andalus.harness.planning;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for an agent plan.
 */
public record PlanId(String value) {
    public PlanId {
        Objects.requireNonNull(value, "PlanId value cannot be null");
    }

    public static PlanId of(String value) {
        return new PlanId(value);
    }

    public static PlanId generate() {
        return new PlanId("plan-" + UUID.randomUUID());
    }
}
