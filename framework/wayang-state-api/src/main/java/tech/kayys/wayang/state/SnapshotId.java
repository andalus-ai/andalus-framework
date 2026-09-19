package tech.kayys.wayang.state;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable unique identifier for a StateSnapshot.
 */
public record SnapshotId(String value) {
    public SnapshotId {
        Objects.requireNonNull(value, "value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value cannot be blank");
        }
    }

    public static SnapshotId of(String value) {
        return new SnapshotId(value);
    }

    public static SnapshotId random() {
        return new SnapshotId(UUID.randomUUID().toString());
    }
}
