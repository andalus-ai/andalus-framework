package tech.kayys.wayang.harness.execution.checkpoint;

import tech.kayys.wayang.harness.execution.state.ExecutionState;

import java.util.Map;
import java.util.Objects;

public record CheckpointContext(
        ExecutionState state,
        String trigger,
        Map<String, Object> metadata
) {
    public CheckpointContext {
        Objects.requireNonNull(state, "state");
        trigger = trigger == null ? "manual" : trigger;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static CheckpointContext of(ExecutionState state, String trigger) {
        return new CheckpointContext(state, trigger, Map.of());
    }
}
