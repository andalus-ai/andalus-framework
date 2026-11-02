package tech.kayys.andalus.execution.recovery;

import tech.kayys.andalus.execution.mobility.ExecutionCursor;

import java.util.Objects;

/**
 * Confirmation that a runtime adapter has reconstructed the checkpoint state.
 * The digest identifies the adapter's canonical restored-state representation.
 */
public record CheckpointRestorationResult(ExecutionCursor cursor, String stateDigest) {
    public CheckpointRestorationResult {
        Objects.requireNonNull(cursor, "cursor cannot be null");
        if (stateDigest == null || stateDigest.isBlank()) {
            throw new IllegalArgumentException("stateDigest must not be blank");
        }
    }
}
