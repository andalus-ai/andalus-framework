package tech.kayys.wayang.harness.execution.checkpoint;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.execution.state.ExecutionState;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record Checkpoint(
        CheckpointId id,
        ExecutionId executionId,
        ExecutionState state,
        Instant createdAt,
        Map<String, Object> metadata
) {
    public Checkpoint {
        id = id == null ? CheckpointId.generate() : id;
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(state, "state");
        createdAt = createdAt == null ? Instant.now() : createdAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static Checkpoint of(ExecutionState state) {
        return new Checkpoint(CheckpointId.generate(), state.executionId(), state, Instant.now(), Map.of());
    }

    public static Checkpoint of(ExecutionState state, Map<String, Object> metadata) {
        return new Checkpoint(CheckpointId.generate(), state.executionId(), state, Instant.now(), metadata);
    }
}
