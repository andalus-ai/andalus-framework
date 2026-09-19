package tech.kayys.wayang.execution.limits;

import java.time.Duration;

/**
 * Composite resource limits enforced within an execution sandbox.
 */
public record ResourceLimits(
        CpuLimit cpu,
        MemoryLimit memory,
        StorageLimit storage,
        ProcessLimit processes,
        Duration maxRuntime
) {

    public ResourceLimits {
        cpu = cpu != null ? cpu : CpuLimit.unlimited();
        memory = memory != null ? memory : MemoryLimit.unlimited();
        storage = storage != null ? storage : StorageLimit.unlimited();
        processes = processes != null ? processes : ProcessLimit.unlimited();
        maxRuntime = maxRuntime != null ? maxRuntime : Duration.ofHours(24);
    }

    public static ResourceLimits unlimited() {
        return new ResourceLimits(
                CpuLimit.unlimited(),
                MemoryLimit.unlimited(),
                StorageLimit.unlimited(),
                ProcessLimit.unlimited(),
                Duration.ofHours(24)
        );
    }

    public static ResourceLimits standard() {
        return new ResourceLimits(
                CpuLimit.of(4.0),
                MemoryLimit.gigabytes(8),
                StorageLimit.gigabytes(20),
                ProcessLimit.of(128, 512),
                Duration.ofMinutes(30)
        );
    }
}
