package tech.kayys.andalus.harness.inference;

import java.util.Optional;

/**
 * Operational resource usage metrics for an inference invocation.
 */
public record InferenceUsage(
        long inputTokens,
        long outputTokens,
        long totalTokens,
        Optional<Long> latencyMillis,
        Optional<Long> estimatedCostMicros
) {
    public InferenceUsage {
        latencyMillis = latencyMillis == null ? Optional.empty() : latencyMillis;
        estimatedCostMicros = estimatedCostMicros == null ? Optional.empty() : estimatedCostMicros;
    }

    public static InferenceUsage empty() {
        return new InferenceUsage(0, 0, 0, Optional.empty(), Optional.empty());
    }

    public static InferenceUsage of(long inputTokens, long outputTokens) {
        return new InferenceUsage(inputTokens, outputTokens, inputTokens + outputTokens, Optional.empty(), Optional.empty());
    }

    public static InferenceUsage of(long inputTokens, long outputTokens, long latencyMillis) {
        return new InferenceUsage(inputTokens, outputTokens, inputTokens + outputTokens, Optional.of(latencyMillis), Optional.empty());
    }
}
