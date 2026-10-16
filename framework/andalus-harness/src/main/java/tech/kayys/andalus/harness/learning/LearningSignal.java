package tech.kayys.andalus.harness.learning;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Versioned, attributable record capturing evidence relevant to agent improvement.
 */
public record LearningSignal(
        String id,
        LearningSignalType type,
        String source,
        String executionId,
        Optional<String> turnId,
        Optional<String> goalId,
        Optional<String> taskId,
        LearningSignalPolarity polarity,
        List<String> evidenceReferences,
        Optional<String> evaluationReference,
        Instant occurredAt,
        Map<String, Object> metadata
) {
    public LearningSignal {
        id = id == null || id.isBlank() ? UUID.randomUUID().toString() : id;
        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId is required");
        }

        source = source == null || source.isBlank() ? "unknown" : source;
        turnId = turnId == null ? Optional.empty() : turnId;
        goalId = goalId == null ? Optional.empty() : goalId;
        taskId = taskId == null ? Optional.empty() : taskId;
        polarity = polarity == null ? LearningSignalPolarity.NEUTRAL : polarity;
        evidenceReferences = evidenceReferences == null ? List.of() : List.copyOf(evidenceReferences);
        evaluationReference = evaluationReference == null ? Optional.empty() : evaluationReference;
        occurredAt = occurredAt == null ? Instant.now() : occurredAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static LearningSignal of(
            LearningSignalType type,
            String source,
            String executionId,
            LearningSignalPolarity polarity,
            List<String> evidenceReferences
    ) {
        return new LearningSignal(
                UUID.randomUUID().toString(),
                type,
                source,
                executionId,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                polarity,
                evidenceReferences,
                Optional.empty(),
                Instant.now(),
                Map.of()
        );
    }

    public static LearningSignal ofEvaluation(
            String source,
            String executionId,
            String evaluationReference,
            LearningSignalPolarity polarity,
            List<String> evidenceReferences
    ) {
        return new LearningSignal(
                UUID.randomUUID().toString(),
                LearningSignalType.EVALUATION,
                source,
                executionId,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                polarity,
                evidenceReferences,
                Optional.ofNullable(evaluationReference),
                Instant.now(),
                Map.of()
        );
    }
}
