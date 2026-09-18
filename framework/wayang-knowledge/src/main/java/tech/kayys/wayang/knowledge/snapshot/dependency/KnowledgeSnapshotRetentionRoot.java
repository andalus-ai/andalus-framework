package tech.kayys.wayang.knowledge.snapshot.dependency;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge snapshot retention root.
 *
 * <p>Its components capture `root id`, `snapshot id`, `type`, `tenant id`, `owner id`, and other values.</p>
 *
 * @param rootId the root id
 * @param snapshotId the snapshot id
 * @param type the type
 * @param tenantId the tenant id
 * @param ownerId the owner id
 * @param createdAt the created at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeSnapshotRetentionRoot(
        String rootId,
        KnowledgeSnapshotId snapshotId,
        KnowledgeSnapshotRetentionRootType type,
        String tenantId,
        String ownerId,
        Instant createdAt,
        Instant expiresAt,
        Map<String, String> metadata
) {

    public KnowledgeSnapshotRetentionRoot {
        Objects.requireNonNull(rootId, "rootId");
        Objects.requireNonNull(snapshotId, "snapshotId");
        Objects.requireNonNull(type, "type");
        createdAt = createdAt == null ? Instant.now() : createdAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean activeAt(Instant now) {
        return expiresAt == null || expiresAt.isAfter(now);
    }
}
