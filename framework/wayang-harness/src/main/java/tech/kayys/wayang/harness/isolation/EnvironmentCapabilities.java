package tech.kayys.wayang.harness.isolation;

import java.util.Set;

/**
 * Declares physical capabilities of an execution environment.
 */
public record EnvironmentCapabilities(
        Set<String> supportedFeatures,
        boolean supportsGpu,
        boolean supportsPersistentStorage
) {

    public EnvironmentCapabilities {
        supportedFeatures = supportedFeatures != null ? Set.copyOf(supportedFeatures) : Set.of();
    }

    public static EnvironmentCapabilities defaults() {
        return new EnvironmentCapabilities(Set.of("standard"), false, true);
    }

    public static EnvironmentCapabilities gpuAccelerated() {
        return new EnvironmentCapabilities(Set.of("standard", "cuda"), true, true);
    }
}
