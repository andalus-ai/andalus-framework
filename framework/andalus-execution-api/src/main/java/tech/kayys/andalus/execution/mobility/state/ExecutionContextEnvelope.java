package tech.kayys.andalus.execution.mobility.state;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Portable execution context envelope carrying state references across runtimes.
 */
public record ExecutionContextEnvelope(
        String executionId,
        long versionSequence,
        Map<String, StateReference> state,
        List<StateReference> artifacts,
        List<StateReference> checkpoints
) {
    public ExecutionContextEnvelope {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        state = state == null ? Map.of() : Map.copyOf(state);
        artifacts = artifacts == null ? List.of() : List.copyOf(artifacts);
        checkpoints = checkpoints == null ? List.of() : List.copyOf(checkpoints);
    }
}
