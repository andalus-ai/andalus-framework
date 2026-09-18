package tech.kayys.wayang.harness.model;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

/**
 * Represents a model usage.
 *
 * <p>Its components capture `input tokens`, `output tokens`, `latency`, `cost`.</p>
 *
 * @param inputTokens the input tokens
 * @param outputTokens the output tokens
 * @param latency the latency
 * @param cost the cost
 */


public record ModelUsage(
        long inputTokens,
        long outputTokens,
        Duration latency,
        Optional<BigDecimal> cost
) {
    public ModelUsage {
        if (latency == null) latency = Duration.ZERO;
        if (cost == null) cost = Optional.empty();
    }

    public long totalTokens() {
        return inputTokens + outputTokens;
    }

    public static ModelUsage of(long inputTokens, long outputTokens, Duration latency) {
        return new ModelUsage(inputTokens, outputTokens, latency, Optional.empty());
    }

    public static ModelUsage empty() {
        return new ModelUsage(0, 0, Duration.ZERO, Optional.empty());
    }
}
