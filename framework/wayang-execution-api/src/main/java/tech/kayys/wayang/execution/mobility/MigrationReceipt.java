package tech.kayys.wayang.execution.mobility;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable receipt of a completed execution migration, suitable for provenance.
 */
public record MigrationReceipt(
        String executionId,
        String sourceRuntimeId,
        String targetRuntimeId,
        String checkpointId,
        String contractHash,
        MigrationReason reason,
        Instant startedAt,
        Instant completedAt
) {
    public MigrationReceipt {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(sourceRuntimeId, "sourceRuntimeId cannot be null");
        Objects.requireNonNull(targetRuntimeId, "targetRuntimeId cannot be null");
        Objects.requireNonNull(checkpointId, "checkpointId cannot be null");
        Objects.requireNonNull(reason, "reason cannot be null");
        if (startedAt == null) {
            startedAt = Instant.now();
        }
        if (completedAt == null) {
            completedAt = Instant.now();
        }
    }
}
