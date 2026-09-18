package tech.kayys.wayang.harness.fabric;

import java.util.Set;

/**
 * Declared capabilities advertised by a worker during registration.
 */
public record WorkerCapabilities(
        Set<String> supportedEnvironments,
        Set<String> supportedExecutors,
        boolean hasGpu,
        long maxCpuCores,
        long maxMemoryMb
) {

    public WorkerCapabilities {
        supportedEnvironments = supportedEnvironments != null ? Set.copyOf(supportedEnvironments) : Set.of();
        supportedExecutors = supportedExecutors != null ? Set.copyOf(supportedExecutors) : Set.of();
    }

    public static WorkerCapabilities standard() {
        return new WorkerCapabilities(
                Set.of("process", "sandbox"),
                Set.of("shell", "generic"),
                false,
                4L,
                8192L
        );
    }

    public static WorkerCapabilities gpuAccelerated() {
        return new WorkerCapabilities(
                Set.of("process", "sandbox", "container"),
                Set.of("shell", "python", "cuda"),
                true,
                16L,
                32768L
        );
    }

    public boolean canExecute(String environment, String executor, boolean requiresGpu) {
        if (requiresGpu && !hasGpu) return false;
        if (environment != null && !supportedEnvironments.contains(environment)) return false;
        if (executor != null && !supportedExecutors.contains(executor)) return false;
        return true;
    }
}
