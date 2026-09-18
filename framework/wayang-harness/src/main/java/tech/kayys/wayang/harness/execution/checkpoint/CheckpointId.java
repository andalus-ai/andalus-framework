package tech.kayys.wayang.harness.execution.checkpoint;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a checkpoint id.
 *
 * <p>Its components capture `value`.</p>
 *
 * @param value the value
 */


public record CheckpointId(String value) {
    public CheckpointId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Checkpoint id must not be blank");
        }
    }

    public static CheckpointId of(String value) {
        return new CheckpointId(value);
    }

    public static CheckpointId generate() {
        return new CheckpointId("cp-" + UUID.randomUUID());
    }
}
