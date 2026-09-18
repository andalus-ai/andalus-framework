package tech.kayys.wayang.harness.model;

import java.time.Duration;

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
