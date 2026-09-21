package tech.kayys.andalus.execution.mobility;

import java.time.Instant;
import java.util.Objects;

/**
 * Phase 1 preparation record for a two-phase execution migration.
 */
public record MigrationPreparation(
        String executionId,
        String sourceRuntimeId,
        String targetRuntimeId,
        String checkpointId,
        MigrationReason reason,
        Instant preparedAt
) {
    public MigrationPreparation {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(sourceRuntimeId, "sourceRuntimeId cannot be null");
        Objects.requireNonNull(targetRuntimeId, "targetRuntimeId cannot be null");
        Objects.requireNonNull(checkpointId, "checkpointId cannot be null");
        if (reason == null) {
            reason = MigrationReason.OPTIMIZATION;
        }
        if (preparedAt == null) {
            preparedAt = Instant.now();
        }
    }
}
