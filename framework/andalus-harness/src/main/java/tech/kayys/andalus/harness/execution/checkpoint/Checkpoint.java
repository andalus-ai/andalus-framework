package tech.kayys.andalus.harness.execution.checkpoint;

import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionState;
import tech.kayys.andalus.harness.journal.JournalPosition;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Durable recovery point for a Andalus Harness execution.
 *
 * <p>A checkpoint represents execution state at a specific
 * position in the execution journal.</p>
 *
 * @param id the id
 * @param executionId the execution id
 * @param state the state
 * @param position the journal position
 * @param createdAt the created at
 * @param metadata the metadata
 */
public record Checkpoint(
        CheckpointId id,
        ExecutionId executionId,
        ExecutionState state,
        JournalPosition position,
        Instant createdAt,
        Map<String, Object> metadata
) {
    public Checkpoint {
        id = id == null ? CheckpointId.generate() : id;
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(state, "state");
        position = position == null ? JournalPosition.beginning() : position;
        if (!executionId.equals(state.executionId())) {
            throw new IllegalArgumentException("Checkpoint executionId does not match execution state");
        }
        createdAt = createdAt == null ? Instant.now() : createdAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static Checkpoint of(ExecutionState state) {
        return new Checkpoint(CheckpointId.generate(), state.executionId(), state, JournalPosition.beginning(), Instant.now(), Map.of());
    }

    public static Checkpoint of(ExecutionState state, JournalPosition position) {
        return new Checkpoint(CheckpointId.generate(), state.executionId(), state, position, Instant.now(), Map.of());
    }

    public static Checkpoint of(ExecutionState state, Map<String, Object> metadata) {
        return new Checkpoint(CheckpointId.generate(), state.executionId(), state, JournalPosition.beginning(), Instant.now(), metadata);
    }

    public static Checkpoint of(ExecutionState state, JournalPosition position, Map<String, Object> metadata) {
        return new Checkpoint(CheckpointId.generate(), state.executionId(), state, position, Instant.now(), metadata);
    }
}
