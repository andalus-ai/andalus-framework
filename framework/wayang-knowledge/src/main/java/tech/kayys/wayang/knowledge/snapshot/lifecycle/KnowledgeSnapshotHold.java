package tech.kayys.wayang.knowledge.snapshot.lifecycle;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge snapshot hold.
 *
 * <p>Its components capture `hold id`, `snapshot id`, `retention class`, `tenant id`, `reason`, and other values.</p>
 *
 * @param holdId the hold id
 * @param snapshotId the snapshot id
 * @param retentionClass the retention class
 * @param tenantId the tenant id
 * @param reason the reason
 * @param createdAt the created at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeSnapshotHold(
        String holdId,
        KnowledgeSnapshotId snapshotId,
        KnowledgeSnapshotRetentionClass retentionClass,
        String tenantId,
        String reason,
        Instant createdAt,
        Instant expiresAt,
        Map<String, String> metadata
) {

    public KnowledgeSnapshotHold {
        Objects.requireNonNull(holdId, "holdId");
        Objects.requireNonNull(snapshotId, "snapshotId");
        Objects.requireNonNull(retentionClass, "retentionClass");
        createdAt = createdAt == null ? Instant.now() : createdAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean activeAt(Instant now) {
        return expiresAt == null || expiresAt.isAfter(now);
    }
}
