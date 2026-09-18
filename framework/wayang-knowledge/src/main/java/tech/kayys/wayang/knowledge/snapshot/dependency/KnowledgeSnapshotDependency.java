package tech.kayys.wayang.knowledge.snapshot.dependency;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge snapshot dependency.
 *
 * <p>Its components capture `dependency id`, `snapshot id`, `type`, `target id`, `target version id`, and other values.</p>
 *
 * @param dependencyId the dependency id
 * @param snapshotId the snapshot id
 * @param type the type
 * @param targetId the target id
 * @param targetVersionId the target version id
 * @param requiredForReplay the required for replay
 * @param requiredForAudit the required for audit
 * @param createdAt the created at
 * @param metadata the metadata
 */


public record KnowledgeSnapshotDependency(
        String dependencyId,
        KnowledgeSnapshotId snapshotId,
        KnowledgeSnapshotDependencyType type,
        String targetId,
        String targetVersionId,
        boolean requiredForReplay,
        boolean requiredForAudit,
        Instant createdAt,
        Map<String, String> metadata
) {

    public KnowledgeSnapshotDependency {
        Objects.requireNonNull(dependencyId, "dependencyId");
        Objects.requireNonNull(snapshotId, "snapshotId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(targetId, "targetId");
        createdAt = createdAt == null ? Instant.now() : createdAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
