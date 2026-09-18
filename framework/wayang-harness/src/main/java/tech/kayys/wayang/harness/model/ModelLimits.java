package tech.kayys.wayang.harness.model;

import java.time.Duration;

/**
 * Represents a model limits.
 *
 * <p>Its components capture `max context tokens`, `max output tokens`, `max latency`.</p>
 *
 * @param maxContextTokens the max context tokens
 * @param maxOutputTokens the max output tokens
 * @param maxLatency the max latency
 */


public record ModelLimits(
        long maxContextTokens,
        long maxOutputTokens,
        Duration maxLatency
) {
    public ModelLimits {
        if (maxLatency == null) {
            maxLatency = Duration.ZERO;
        }
    }

    public static ModelLimits of(long maxContextTokens, long maxOutputTokens) {
        return new ModelLimits(maxContextTokens, maxOutputTokens, Duration.ZERO);
    }
}
