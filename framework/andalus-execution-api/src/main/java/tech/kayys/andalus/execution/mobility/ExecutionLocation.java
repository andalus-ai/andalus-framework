package tech.kayys.andalus.execution.mobility;

import java.time.Instant;
import java.util.Objects;

/**
 * Mutable execution location representing the currently bound runtime.
 */
public record ExecutionLocation(
        String runtimeId,
        String runtimeBinding,
        Instant boundAt
) {
    public ExecutionLocation {
        Objects.requireNonNull(runtimeId, "runtimeId cannot be null");
        Objects.requireNonNull(runtimeBinding, "runtimeBinding cannot be null");
        if (boundAt == null) {
            boundAt = Instant.now();
        }
    }

    public static ExecutionLocation of(String runtimeId, String runtimeBinding) {
        return new ExecutionLocation(runtimeId, runtimeBinding, Instant.now());
    }
}
