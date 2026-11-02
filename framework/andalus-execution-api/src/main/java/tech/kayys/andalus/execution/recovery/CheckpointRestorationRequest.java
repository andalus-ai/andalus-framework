package tech.kayys.andalus.execution.recovery;

import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.checkpoint.ExecutionCheckpoint;
import tech.kayys.andalus.execution.mobility.ExecutionDurableState;

import java.util.Objects;

/**
 * Provider-neutral input to a runtime-specific checkpoint restoration adapter.
 * The adapter owns payload decoding and runtime reconstruction.
 */
public record CheckpointRestorationRequest(
        ExecutionCheckpoint checkpoint,
        ExecutionDurableState durableState,
        AttemptId attemptId,
        String targetWorkerId
) {
    public CheckpointRestorationRequest {
        Objects.requireNonNull(checkpoint, "checkpoint cannot be null");
        Objects.requireNonNull(durableState, "durableState cannot be null");
        Objects.requireNonNull(attemptId, "attemptId cannot be null");
        if (targetWorkerId == null || targetWorkerId.isBlank()) {
            throw new IllegalArgumentException("targetWorkerId must not be blank");
        }
        if (!checkpoint.executionId().equals(durableState.identity().executionId())) {
            throw new IllegalArgumentException("checkpoint and durable state execution IDs must match");
        }
    }
}
