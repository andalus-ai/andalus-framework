package tech.kayys.wayang.execution.snapshot;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Reference metadata to an exported or captured sandbox snapshot.
 */
public record SnapshotReference(
        String snapshotId,
        Instant createdAt,
        Map<String, String> metadata
) {

    public SnapshotReference {
        Objects.requireNonNull(snapshotId, "snapshotId cannot be null");
        createdAt = createdAt != null ? createdAt : Instant.now();
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public static SnapshotReference generate() {
        return new SnapshotReference("snap-" + UUID.randomUUID(), Instant.now(), Map.of());
    }
}
