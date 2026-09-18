package tech.kayys.wayang.harness.fabric;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a work assignment dispatched to a worker.
 */
public record AssignmentId(String value) {

    public AssignmentId {
        Objects.requireNonNull(value, "AssignmentId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("AssignmentId value cannot be blank");
        }
    }

    public static AssignmentId of(String value) {
        return new AssignmentId(value);
    }

    public static AssignmentId generate() {
        return new AssignmentId("assign-" + UUID.randomUUID());
    }
}
