package tech.kayys.wayang.harness.security.capability;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a granted capability lease.
 */
public record CapabilityLeaseId(String value) {
    public CapabilityLeaseId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("CapabilityLeaseId cannot be blank");
        }
    }

    public static CapabilityLeaseId of(String value) {
        return new CapabilityLeaseId(value);
    }

    public static CapabilityLeaseId generate() {
        return new CapabilityLeaseId("cap-lease-" + UUID.randomUUID());
    }
}
