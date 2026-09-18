package tech.kayys.wayang.harness.consistency.checkpoint;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a checkpoint.
 */
public record CheckpointId(String value) {

    public CheckpointId {
        Objects.requireNonNull(value, "CheckpointId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("CheckpointId value cannot be blank");
        }
    }

    public static CheckpointId of(String value) {
        return new CheckpointId(value);
    }

    public static CheckpointId generate() {
        return new CheckpointId("cp-" + UUID.randomUUID());
    }
}
