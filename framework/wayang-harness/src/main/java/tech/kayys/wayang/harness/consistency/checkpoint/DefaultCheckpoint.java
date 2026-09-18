package tech.kayys.wayang.harness.consistency.checkpoint;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable reference record implementing {@link Checkpoint}.
 */
public record DefaultCheckpoint(
        CheckpointId id,
        ExecutionId executionId,
        long sequence,
        CheckpointManifest manifest,
        Instant createdAt
) implements Checkpoint {

    public DefaultCheckpoint {
        Objects.requireNonNull(id, "CheckpointId cannot be null");
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        Objects.requireNonNull(manifest, "CheckpointManifest cannot be null");
        createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static DefaultCheckpoint of(
            CheckpointId id,
            ExecutionId executionId,
            long sequence,
            CheckpointManifest manifest
    ) {
        return new DefaultCheckpoint(id, executionId, sequence, manifest, Instant.now());
    }
}
