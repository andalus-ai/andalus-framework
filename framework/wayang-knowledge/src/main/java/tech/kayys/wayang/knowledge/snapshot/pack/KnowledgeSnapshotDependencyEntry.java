package tech.kayys.wayang.knowledge.snapshot.pack;

import java.util.Map;

/**
 * Represents a knowledge snapshot dependency entry.
 *
 * <p>Its components capture `dependency id`, `type`, `target id`, `target version id`, `required for replay`, and other values.</p>
 *
 * @param dependencyId the dependency id
 * @param type the type
 * @param targetId the target id
 * @param targetVersionId the target version id
 * @param requiredForReplay the required for replay
 * @param requiredForAudit the required for audit
 * @param metadata the metadata
 */


public record KnowledgeSnapshotDependencyEntry(
        String dependencyId,
        String type,
        String targetId,
        String targetVersionId,
        boolean requiredForReplay,
        boolean requiredForAudit,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotDependencyEntry {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
