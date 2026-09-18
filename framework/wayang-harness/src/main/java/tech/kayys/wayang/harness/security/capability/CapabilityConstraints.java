package tech.kayys.wayang.harness.security.capability;

import java.util.Map;

/**
 * Scoped boundary constraints attached to a capability grant (e.g. filesystem path prefix, allowed hosts).
 */
public record CapabilityConstraints(
        Map<String, String> parameters
) {
    public CapabilityConstraints {
        parameters = parameters != null ? Map.copyOf(parameters) : Map.of();
    }

    public static CapabilityConstraints unconstrained() {
        return new CapabilityConstraints(Map.of());
    }

    public static CapabilityConstraints of(String key, String value) {
        return new CapabilityConstraints(Map.of(key, value));
    }
}
