package tech.kayys.andalus.skill.analytics;

import java.time.Duration;

/**
 * Aggregated operational measurements for a set of skill invocations.
 */
public record SkillStatistics(
        long executions,
        long successes,
        long failures,
        long cancellations,
        long timeouts,
        Duration averageDuration,
        Duration p50Duration,
        Duration p95Duration,
        Duration p99Duration,
        long capabilityFailures,
        long policyRejections
) {

    public SkillStatistics {
        averageDuration = averageDuration == null ? Duration.ZERO : averageDuration;
        p50Duration     = p50Duration     == null ? Duration.ZERO : p50Duration;
        p95Duration     = p95Duration     == null ? Duration.ZERO : p95Duration;
        p99Duration     = p99Duration     == null ? Duration.ZERO : p99Duration;
    }

    public double successRate() {
        return executions == 0 ? 1.0 : (double) successes / executions;
    }

    public double failureRate() {
        return executions == 0 ? 0.0 : (double) failures / executions;
    }

    public static SkillStatistics empty() {
        return new SkillStatistics(0, 0, 0, 0, 0, Duration.ZERO, Duration.ZERO, Duration.ZERO, Duration.ZERO, 0, 0);
    }
}
