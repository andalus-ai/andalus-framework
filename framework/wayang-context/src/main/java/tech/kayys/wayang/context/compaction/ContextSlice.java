package tech.kayys.wayang.context.compaction;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Representation of an active context window slice with token budgeting.
 */
public record ContextSlice(
    String sliceId,
    String systemPrompt,
    List<String> activeTurns,
    List<String> historicalSummaries,
    long estimatedTokens,
    long maxTokenCapacity
) implements Serializable {

    public ContextSlice {
        Objects.requireNonNull(sliceId, "sliceId cannot be null");
        Objects.requireNonNull(systemPrompt, "systemPrompt cannot be null");
        activeTurns = activeTurns != null ? List.copyOf(activeTurns) : List.of();
        historicalSummaries = historicalSummaries != null ? List.copyOf(historicalSummaries) : List.of();
        if (maxTokenCapacity <= 0) {
            throw new IllegalArgumentException("maxTokenCapacity must be positive");
        }
    }

    public boolean requiresCompaction(double thresholdRatio) {
        return ((double) estimatedTokens / (double) maxTokenCapacity) >= thresholdRatio;
    }

    public static ContextSlice of(String sliceId, String systemPrompt, List<String> activeTurns, long estimatedTokens, long maxTokenCapacity) {
        return new ContextSlice(sliceId, systemPrompt, activeTurns, List.of(), estimatedTokens, maxTokenCapacity);
    }
}
