package tech.kayys.andalus.harness.inference;

import java.util.Map;
import java.util.Optional;

/**
 * Budget and resource constraints governing an inference operation.
 */
public record InferenceConstraints(
        long maxInputTokens,
        long maxOutputTokens,
        long maxTotalTokens,
        long maxLatencyMillis,
        Optional<Long> maxCostMicros,
        Map<String, Object> attributes
) {
    public InferenceConstraints {
        maxCostMicros = maxCostMicros == null ? Optional.empty() : maxCostMicros;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static InferenceConstraints defaults() {
        return new InferenceConstraints(8192, 2048, 10240, 30000, Optional.empty(), Map.of());
    }

    public static InferenceConstraints bounded(long maxTotalTokens, long maxLatencyMillis) {
        return new InferenceConstraints(
                maxTotalTokens / 2,
                maxTotalTokens / 2,
                maxTotalTokens,
                maxLatencyMillis,
                Optional.empty(),
                Map.of()
        );
    }
}
