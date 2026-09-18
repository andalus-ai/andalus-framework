package tech.kayys.wayang.harness.workspace.v3;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a workspace snapshot.
 */
public record SnapshotId(String value) {
    public SnapshotId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("SnapshotId cannot be blank");
        }
    }

    public static SnapshotId of(String value) {
        return new SnapshotId(value);
    }

    public static SnapshotId generate() {
        return new SnapshotId("snap-ws-" + UUID.randomUUID());
    }
}
