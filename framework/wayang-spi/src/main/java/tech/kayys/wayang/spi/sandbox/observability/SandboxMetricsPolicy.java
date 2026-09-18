package tech.kayys.wayang.spi.sandbox.observability;

import java.time.Duration;

public record SandboxMetricsPolicy(
        boolean enabled,
        Duration sampleInterval
) {

    public SandboxMetricsPolicy {
        if (sampleInterval == null) {
            throw new IllegalArgumentException(
                    "sampleInterval must not be null");
        }

        if (sampleInterval.isZero()
                || sampleInterval.isNegative()) {

            throw new IllegalArgumentException(
                    "sampleInterval must be positive");
        }
    }

    public static SandboxMetricsPolicy disabled() {
        return new SandboxMetricsPolicy(
                false,
                Duration.ofSeconds(30));
    }

    public static SandboxMetricsPolicy defaults() {
        return new SandboxMetricsPolicy(
                true,
                Duration.ofSeconds(10));
    }
}
