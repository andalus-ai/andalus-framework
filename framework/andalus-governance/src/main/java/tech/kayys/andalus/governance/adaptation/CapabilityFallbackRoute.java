package tech.kayys.andalus.governance.adaptation;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Fallback routing configuration for a tool or capability when degraded or failing.
 */
public record CapabilityFallbackRoute(
    String originalCapability,
    List<String> alternativeCapabilities,
    int maxConsecutiveFailures,
    boolean requiresHumanApproval
) implements Serializable {

    public CapabilityFallbackRoute {
        Objects.requireNonNull(originalCapability, "originalCapability cannot be null");
        alternativeCapabilities = alternativeCapabilities != null ? List.copyOf(alternativeCapabilities) : List.of();
        if (maxConsecutiveFailures <= 0) {
            throw new IllegalArgumentException("maxConsecutiveFailures must be positive");
        }
    }

    public static CapabilityFallbackRoute of(String originalCapability, List<String> alternatives, int maxFailures) {
        return new CapabilityFallbackRoute(originalCapability, alternatives, maxFailures, false);
    }
}
