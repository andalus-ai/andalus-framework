package tech.kayys.andalus.harness.state;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable snapshot of the overall agent state for an execution at a given revision.
 */
public record AgentStateSnapshot(
        String executionId,
        long revision,
        Optional<String> activeGoalId,
        Optional<String> activeTaskId,
        List<BeliefReference> beliefs,
        List<String> unresolvedQuestions,
        List<String> observationReferences,
        Optional<String> checkpointReference,
        Instant capturedAt,
        Map<String, Object> metadata
) {
    public AgentStateSnapshot {
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId is required");
        }
        if (revision < 0) {
            throw new IllegalArgumentException("revision must be non-negative");
        }

        activeGoalId = activeGoalId == null ? Optional.empty() : activeGoalId;
        activeTaskId = activeTaskId == null ? Optional.empty() : activeTaskId;
        beliefs = beliefs == null ? List.of() : List.copyOf(beliefs);
        unresolvedQuestions = unresolvedQuestions == null ? List.of() : List.copyOf(unresolvedQuestions);
        observationReferences = observationReferences == null ? List.of() : List.copyOf(observationReferences);
        checkpointReference = checkpointReference == null ? Optional.empty() : checkpointReference;
        capturedAt = capturedAt == null ? Instant.now() : capturedAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static AgentStateSnapshot of(
            String executionId,
            long revision,
            String activeGoalId,
            String activeTaskId,
            List<BeliefReference> beliefs
    ) {
        return new AgentStateSnapshot(
                executionId,
                revision,
                Optional.ofNullable(activeGoalId),
                Optional.ofNullable(activeTaskId),
                beliefs,
                List.of(),
                List.of(),
                Optional.empty(),
                Instant.now(),
                Map.of()
        );
    }
}
