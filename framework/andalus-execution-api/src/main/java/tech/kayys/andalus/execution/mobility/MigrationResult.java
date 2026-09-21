package tech.kayys.andalus.execution.mobility;

import java.time.Instant;
import java.util.Objects;

/**
 * Result of an execution migration attempt.
 */
public record MigrationResult(
        String executionId,
        boolean success,
        String sourceRuntimeId,
        String targetRuntimeId,
        String errorMessage,
        Instant completedAt
) {
    public MigrationResult {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        if (completedAt == null) {
            completedAt = Instant.now();
        }
    }

    public static MigrationResult success(String executionId, String source, String target) {
        return new MigrationResult(executionId, true, source, target, null, Instant.now());
    }

    public static MigrationResult failure(String executionId, String source, String target, String error) {
        return new MigrationResult(executionId, false, source, target, error, Instant.now());
    }
}
