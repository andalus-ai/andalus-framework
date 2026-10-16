package tech.kayys.andalus.harness.learning;

import java.util.Optional;

/**
 * Filter criteria for querying learning signals.
 */
public record LearningSignalQuery(
        Optional<String> executionId,
        Optional<LearningSignalType> type,
        Optional<LearningSignalPolarity> polarity,
        int limit
) {
    public LearningSignalQuery {
        executionId = executionId == null ? Optional.empty() : executionId;
        type = type == null ? Optional.empty() : type;
        polarity = polarity == null ? Optional.empty() : polarity;
        limit = limit <= 0 ? 50 : limit;
    }

    public static LearningSignalQuery forExecution(String executionId) {
        return new LearningSignalQuery(Optional.ofNullable(executionId), Optional.empty(), Optional.empty(), 50);
    }

    public static LearningSignalQuery forType(LearningSignalType type) {
        return new LearningSignalQuery(Optional.empty(), Optional.ofNullable(type), Optional.empty(), 50);
    }

    public static LearningSignalQuery all() {
        return new LearningSignalQuery(Optional.empty(), Optional.empty(), Optional.empty(), 50);
    }
}
