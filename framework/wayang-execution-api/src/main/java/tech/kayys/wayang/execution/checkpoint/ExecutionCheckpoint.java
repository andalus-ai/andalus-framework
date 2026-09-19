package tech.kayys.wayang.execution.checkpoint;

import tech.kayys.wayang.execution.attempt.AttemptId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * First-class durable execution checkpoint.
 */
public record ExecutionCheckpoint(
        String checkpointId,
        String executionId,
        AttemptId attemptId,
        long sequence,
        byte[] payload,
        CheckpointManifest manifest,
        Instant timestamp,
        Map<String, Object> metadata
) {
    public ExecutionCheckpoint {
        Objects.requireNonNull(checkpointId, "checkpointId cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(attemptId, "attemptId cannot be null");
        Objects.requireNonNull(payload, "payload cannot be null");
        Objects.requireNonNull(manifest, "manifest cannot be null");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
