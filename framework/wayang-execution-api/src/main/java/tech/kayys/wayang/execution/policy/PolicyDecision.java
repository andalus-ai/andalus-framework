package tech.kayys.wayang.execution.policy;

import java.util.Map;
import java.util.Objects;

/**
 * High-level policy decision outcome for a sandbox operation.
 */
public record PolicyDecision(
        boolean permitted,
        String reason,
        Map<String, Object> metadata
) {

    public PolicyDecision {
        reason = reason != null ? reason : "";
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public static PolicyDecision allow(String reason) {
        return new PolicyDecision(true, reason, Map.of());
    }

    public static PolicyDecision deny(String reason) {
        return new PolicyDecision(false, reason, Map.of());
    }
}
