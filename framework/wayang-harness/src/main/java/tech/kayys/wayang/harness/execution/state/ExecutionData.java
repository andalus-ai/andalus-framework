package tech.kayys.wayang.harness.execution.state;

import java.util.Map;

/**
 * Represents a execution data.
 *
 * <p>Its components capture `state`, `metadata`.</p>
 *
 * @param state the state
 * @param metadata the metadata
 */


public record ExecutionData(
        Map<String, Object> state,
        Map<String, Object> metadata
) {
    public ExecutionData {
        state = state == null ? Map.of() : Map.copyOf(state);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ExecutionData empty() {
        return new ExecutionData(Map.of(), Map.of());
    }
}
