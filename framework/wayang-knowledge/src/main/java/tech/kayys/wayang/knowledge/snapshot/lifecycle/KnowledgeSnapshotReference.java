package tech.kayys.wayang.knowledge.snapshot.lifecycle;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge snapshot reference.
 *
 * <p>Its components capture `reference id`, `snapshot id`, `type`, `owner id`, `tenant id`, and other values.</p>
 *
 * @param referenceId the reference id
 * @param snapshotId the snapshot id
 * @param type the type
 * @param ownerId the owner id
 * @param tenantId the tenant id
 * @param createdAt the created at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeSnapshotReference(
        String referenceId,
        KnowledgeSnapshotId snapshotId,
        KnowledgeSnapshotReferenceType type,
        String ownerId,
        String tenantId,
        Instant createdAt,
        Instant expiresAt,
        Map<String, String> metadata
) {

    public KnowledgeSnapshotReference {
        Objects.requireNonNull(referenceId, "referenceId");
        Objects.requireNonNull(snapshotId, "snapshotId");
        Objects.requireNonNull(type, "type");
        createdAt = createdAt == null ? Instant.now() : createdAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean isExpired(Instant now) {
        return expiresAt != null && !expiresAt.isAfter(now);
    }

    public boolean isActive(Instant now) {
        return !isExpired(now);
    }
}
