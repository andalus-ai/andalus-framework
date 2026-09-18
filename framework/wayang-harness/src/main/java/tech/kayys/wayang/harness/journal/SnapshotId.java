package tech.kayys.wayang.harness.journal;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for an execution snapshot.
 */
public record SnapshotId(String value) {

    public SnapshotId {
        Objects.requireNonNull(value, "SnapshotId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("SnapshotId value cannot be blank");
        }
    }

    public static SnapshotId of(String value) {
        return new SnapshotId(value);
    }

    public static SnapshotId generate() {
        return new SnapshotId("snap-" + UUID.randomUUID());
    }
}
