package tech.kayys.andalus.execution.checkpoint;

import tech.kayys.andalus.execution.attempt.AttemptId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * First-class durable execution checkpoint.
 *
 * <p>The payload is defensively copied on construction and access so callers
 * cannot mutate persisted checkpoint contents after integrity validation.</p>
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
        requireText(checkpointId, "checkpointId");
        requireText(executionId, "executionId");
        Objects.requireNonNull(attemptId, "attemptId cannot be null");
        Objects.requireNonNull(payload, "payload cannot be null");
        Objects.requireNonNull(manifest, "manifest cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");
        if (sequence < 1) {
            throw new IllegalArgumentException("sequence must be greater than zero");
        }
        if (!checkpointId.equals(manifest.checkpointId())) {
            throw new IllegalArgumentException("checkpointId must match manifest checkpointId");
        }
        if (sequence != manifest.sequence()) {
            throw new IllegalArgumentException("sequence must match manifest sequence");
        }

        payload = payload.clone();
        if (!manifest.integrity().verify(payload)) {
            throw new IllegalArgumentException("checkpoint payload does not match manifest integrity checksum");
        }
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    @Override
    public byte[] payload() {
        return payload.clone();
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
