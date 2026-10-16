package tech.kayys.andalus.skill.analytics;

import java.time.Duration;
import java.util.Optional;

/**
 * High-level operational health snapshot for a Skill.
 */
public record SkillHealth(
        long executionCount,
        double successRate,
        Duration p95Latency,
        double timeoutRate,
        double failureRate,
        Optional<Double> averageCost
) {

    public SkillHealth {
        p95Latency  = p95Latency  == null ? Duration.ZERO     : p95Latency;
        averageCost = averageCost == null ? Optional.empty()  : averageCost;
    }
}
