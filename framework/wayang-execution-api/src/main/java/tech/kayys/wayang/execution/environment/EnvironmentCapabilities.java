package tech.kayys.wayang.execution.environment;

import java.util.Set;

/**
 * Declares capabilities supported by an execution environment.
 */
public record EnvironmentCapabilities(
        boolean networkAllowed,
        boolean filesystemAllowed,
        boolean processAllowed,
        boolean gpuAccelerated,
        Set<String> customCapabilities
) {

    public EnvironmentCapabilities {
        customCapabilities = customCapabilities != null ? Set.copyOf(customCapabilities) : Set.of();
    }

    public static EnvironmentCapabilities defaults() {
        return new EnvironmentCapabilities(false, true, true, false, Set.of());
    }

    public static EnvironmentCapabilities unconstrained() {
        return new EnvironmentCapabilities(true, true, true, true, Set.of());
    }

    public static EnvironmentCapabilities restricted() {
        return new EnvironmentCapabilities(false, false, false, false, Set.of());
    }

    public static EnvironmentCapabilities withGpu() {
        return new EnvironmentCapabilities(false, true, true, true, Set.of("gpu"));
    }
}
