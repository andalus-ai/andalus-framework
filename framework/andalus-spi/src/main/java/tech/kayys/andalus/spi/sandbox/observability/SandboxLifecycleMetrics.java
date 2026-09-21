package tech.kayys.andalus.spi.sandbox.observability;

import java.time.Duration;

public record SandboxLifecycleMetrics(
        Duration creationDuration,
        Duration startupDuration,
        Duration executionDuration,
        Duration shutdownDuration,
        Duration destructionDuration
) {
    public static SandboxLifecycleMetrics empty() {
        return new SandboxLifecycleMetrics(
                Duration.ZERO,
                Duration.ZERO,
                Duration.ZERO,
                Duration.ZERO,
                Duration.ZERO);
    }
}
