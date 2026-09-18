package tech.kayys.wayang.harness.consistency.state;

import tech.kayys.wayang.harness.consistency.checkpoint.CheckpointId;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable reference record implementation of {@link ExecutionRecord}.
 */
public record DefaultExecutionRecord(
        ExecutionId id,
        ExecutionState state,
        long version,
        Instant createdAt,
        Instant updatedAt,
        Optional<CheckpointId> checkpoint,
        Optional<FailureInfo> failure
) implements ExecutionRecord {

    public DefaultExecutionRecord {
        Objects.requireNonNull(id, "ExecutionId cannot be null");
        Objects.requireNonNull(state, "ExecutionState cannot be null");
        Objects.requireNonNull(createdAt, "createdAt cannot be null");
        Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
        checkpoint = checkpoint != null ? checkpoint : Optional.empty();
        failure = failure != null ? failure : Optional.empty();
    }

    public static DefaultExecutionRecord create(ExecutionId id) {
        Instant now = Instant.now();
        return new DefaultExecutionRecord(
                id,
                ExecutionState.CREATED,
                1L,
                now,
                now,
                Optional.empty(),
                Optional.empty()
        );
    }

    public DefaultExecutionRecord withState(ExecutionState newState) {
        return new DefaultExecutionRecord(
                id,
                newState,
                version + 1,
                createdAt,
                Instant.now(),
                checkpoint,
                failure
        );
    }

    public DefaultExecutionRecord withCheckpoint(CheckpointId cpId) {
        return new DefaultExecutionRecord(
                id,
                state,
                version + 1,
                createdAt,
                Instant.now(),
                Optional.ofNullable(cpId),
                failure
        );
    }

    public DefaultExecutionRecord withFailure(FailureInfo fail) {
        return new DefaultExecutionRecord(
                id,
                ExecutionState.FAILED,
                version + 1,
                createdAt,
                Instant.now(),
                checkpoint,
                Optional.ofNullable(fail)
        );
    }
}
