package tech.kayys.wayang.harness.authority;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a coordinator node in a high-availability cluster.
 */
public record CoordinatorId(String value) {

    public CoordinatorId {
        Objects.requireNonNull(value, "CoordinatorId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("CoordinatorId value cannot be blank");
        }
    }

    public static CoordinatorId of(String value) {
        return new CoordinatorId(value);
    }

    public static CoordinatorId generate() {
        return new CoordinatorId("coord-" + UUID.randomUUID());
    }
}
