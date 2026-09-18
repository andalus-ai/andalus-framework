package tech.kayys.wayang.harness.isolation;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a revocable resource lease.
 */
public record LeaseId(String value) {

    public LeaseId {
        Objects.requireNonNull(value, "LeaseId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("LeaseId value cannot be blank");
        }
    }

    public static LeaseId of(String value) {
        return new LeaseId(value);
    }

    public static LeaseId generate() {
        return new LeaseId("lease-" + UUID.randomUUID());
    }
}
