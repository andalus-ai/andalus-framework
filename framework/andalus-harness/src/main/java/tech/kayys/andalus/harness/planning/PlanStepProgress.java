package tech.kayys.andalus.harness.planning;

import tech.kayys.andalus.harness.task.TaskState;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Tracks the execution progress and state of an individual step in an agent plan.
 */
public record PlanStepProgress(
        PlanStepId stepId,
        TaskState state,
        long revision,
        Instant updatedAt,
        Map<String, Object> attributes
) {
    public PlanStepProgress {
        Objects.requireNonNull(stepId, "stepId is required");
        Objects.requireNonNull(state, "state is required");
        if (revision < 0) {
            throw new IllegalArgumentException("revision must be non-negative");
        }
        updatedAt = updatedAt == null ? Instant.now() : updatedAt;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
