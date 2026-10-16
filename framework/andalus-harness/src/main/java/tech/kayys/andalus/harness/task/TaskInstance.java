package tech.kayys.andalus.harness.task;

import tech.kayys.andalus.harness.goal.GoalId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a concrete occurrence of a task during an agent execution,
 * tracking lifecycle state, revisions, and results.
 */
public record TaskInstance(
        TaskId id,
        TaskDefinition definition,
        String executionId,
        Optional<GoalId> goalId,
        Optional<TaskId> parentTaskId,
        TaskState state,
        long revision,
        Optional<TaskResult> result,
        Instant createdAt,
        Instant updatedAt,
        Map<String, Object> attributes
) {
    public TaskInstance {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(definition, "definition is required");
        Objects.requireNonNull(executionId, "executionId is required");
        Objects.requireNonNull(state, "state is required");
        goalId = goalId == null ? Optional.empty() : goalId;
        parentTaskId = parentTaskId == null ? Optional.empty() : parentTaskId;
        result = result == null ? Optional.empty() : result;
        createdAt = createdAt == null ? Instant.now() : createdAt;
        updatedAt = updatedAt == null ? Instant.now() : updatedAt;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        if (revision < 0) {
            throw new IllegalArgumentException("revision must be non-negative");
        }
    }

    public TaskInstance transitionTo(TaskState newState) {
        return new TaskInstance(
                id,
                definition,
                executionId,
                goalId,
                parentTaskId,
                newState,
                revision + 1,
                result,
                createdAt,
                Instant.now(),
                attributes
        );
    }

    public TaskInstance complete(TaskResult taskResult) {
        return new TaskInstance(
                id,
                definition,
                executionId,
                goalId,
                parentTaskId,
                taskResult.state(),
                revision + 1,
                Optional.of(taskResult),
                createdAt,
                Instant.now(),
                attributes
        );
    }
}
