package tech.kayys.andalus.harness.learning;

import java.util.List;
import java.util.Map;

/**
 * Result of querying learning signals.
 */
public record LearningSignalQueryResult(
        List<LearningSignal> signals,
        int totalCount,
        Map<String, Object> metadata
) {
    public LearningSignalQueryResult {
        signals = signals == null ? List.of() : List.copyOf(signals);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static LearningSignalQueryResult of(List<LearningSignal> signals) {
        int count = signals != null ? signals.size() : 0;
        return new LearningSignalQueryResult(signals, count, Map.of());
    }

    public static LearningSignalQueryResult empty() {
        return new LearningSignalQueryResult(List.of(), 0, Map.of());
    }
}
