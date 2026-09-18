package tech.kayys.wayang.harness.environment.v3.resource;

import java.util.Map;

/**
 * Operating constraints attached to a resource lease.
 */
public record LeaseConstraints(
        long maxCapacity,
        Map<String, String> restrictions
) {
    public LeaseConstraints {
        restrictions = restrictions != null ? Map.copyOf(restrictions) : Map.of();
    }

    public static LeaseConstraints unconstrained() {
        return new LeaseConstraints(Long.MAX_VALUE, Map.of());
    }

    public static LeaseConstraints ofCapacity(long maxCapacity) {
        return new LeaseConstraints(maxCapacity, Map.of());
    }
}
