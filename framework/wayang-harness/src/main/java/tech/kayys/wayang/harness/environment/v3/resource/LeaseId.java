package tech.kayys.wayang.harness.environment.v3.resource;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a granted resource lease.
 */
public record LeaseId(String value) {
    public LeaseId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("LeaseId cannot be blank");
        }
    }

    public static LeaseId of(String value) {
        return new LeaseId(value);
    }

    public static LeaseId generate() {
        return new LeaseId("lease-" + UUID.randomUUID());
    }
}
