package tech.kayys.andalus.harness.goal;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a goal instance pursued during an agent execution, tracking lifecycle state and revision.
 */
public record GoalInstance(
        GoalId id,
        GoalDefinition definition,
        String executionId,
        Optional<GoalId> parentGoalId,
        GoalState state,
        Instant createdAt,
        long revision,
        Map<String, Object> attributes
) {
    public GoalInstance {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(definition, "definition is required");
        Objects.requireNonNull(executionId, "executionId is required");
        Objects.requireNonNull(state, "state is required");
        createdAt = createdAt == null ? Instant.now() : createdAt;
        parentGoalId = parentGoalId == null ? Optional.empty() : parentGoalId;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        if (revision < 0) {
            throw new IllegalArgumentException("revision must be non-negative");
        }
    }

    public GoalInstance transitionTo(GoalState newState) {
        return new GoalInstance(
                id,
                definition,
                executionId,
                parentGoalId,
                newState,
                createdAt,
                revision + 1,
                attributes
        );
    }
}
