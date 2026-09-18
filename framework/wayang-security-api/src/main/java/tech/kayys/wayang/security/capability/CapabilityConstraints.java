package tech.kayys.wayang.security.capability;

import java.util.Map;
import java.util.Objects;

/**
 * Encapsulates execution and boundary constraints applied to a capability lease.
 */
public record CapabilityConstraints(Map<String, Object> constraints) {

    public CapabilityConstraints {
        constraints = constraints != null ? Map.copyOf(constraints) : Map.of();
    }

    public static CapabilityConstraints unconstrained() {
        return new CapabilityConstraints(Map.of());
    }

    public static CapabilityConstraints of(String key, Object value) {
        return new CapabilityConstraints(Map.of(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value")));
    }

    public static CapabilityConstraints of(Map<String, Object> constraints) {
        return new CapabilityConstraints(constraints);
    }
}
