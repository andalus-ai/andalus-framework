package tech.kayys.wayang.harness.context;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a context snapshot.
 *
 * <p>Its components capture `snapshot id`, `timestamp`, `assembled`, `metadata`.</p>
 *
 * @param snapshotId the snapshot id
 * @param timestamp the timestamp
 * @param assembled the assembled
 * @param metadata the metadata
 */


public record ContextSnapshot(
        String snapshotId,
        Instant timestamp,
        AssembledContext assembled,
        Map<String, Object> metadata
) {
    public ContextSnapshot {
        snapshotId = snapshotId == null ? "ctxsnap-" + UUID.randomUUID() : snapshotId;
        timestamp = timestamp == null ? Instant.now() : timestamp;
        Objects.requireNonNull(assembled, "assembled");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
