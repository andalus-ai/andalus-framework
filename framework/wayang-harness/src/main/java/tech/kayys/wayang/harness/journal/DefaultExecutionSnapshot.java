package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable reference record implementing {@link ExecutionSnapshot}.
 */
public record DefaultExecutionSnapshot(
        SnapshotId id,
        ExecutionId executionId,
        JournalPosition position,
        SnapshotPayload payload,
        Instant createdAt
) implements ExecutionSnapshot {

    public DefaultExecutionSnapshot {
        Objects.requireNonNull(id, "SnapshotId cannot be null");
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        Objects.requireNonNull(position, "JournalPosition cannot be null");
        Objects.requireNonNull(payload, "SnapshotPayload cannot be null");
        createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static DefaultExecutionSnapshot of(
            ExecutionId executionId,
            JournalPosition position,
            SnapshotPayload payload
    ) {
        return new DefaultExecutionSnapshot(
                SnapshotId.generate(),
                executionId,
                position,
                payload,
                Instant.now()
        );
    }
}
