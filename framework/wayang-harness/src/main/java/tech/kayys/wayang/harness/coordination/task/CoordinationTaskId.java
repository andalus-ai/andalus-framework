package tech.kayys.wayang.harness.coordination.task;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for an assigned agent task in the coordination runtime.
 */
public record CoordinationTaskId(String value) {
    public CoordinationTaskId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("CoordinationTaskId cannot be blank");
        }
    }

    public static CoordinationTaskId of(String value) {
        return new CoordinationTaskId(value);
    }

    public static CoordinationTaskId generate() {
        return new CoordinationTaskId("coord-task-" + UUID.randomUUID());
    }
}
