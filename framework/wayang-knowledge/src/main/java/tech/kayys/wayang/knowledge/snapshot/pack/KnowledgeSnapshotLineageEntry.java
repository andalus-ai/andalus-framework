package tech.kayys.wayang.knowledge.snapshot.pack;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge snapshot lineage entry.
 *
 * <p>Its components capture `lineage id`, `source knowledge id`, `target knowledge id`, `relation type`, `created by`, and other values.</p>
 *
 * @param lineageId the lineage id
 * @param sourceKnowledgeId the source knowledge id
 * @param targetKnowledgeId the target knowledge id
 * @param relationType the relation type
 * @param createdBy the created by
 * @param createdAt the created at
 * @param requiredForReplay the required for replay
 * @param metadata the metadata
 */


public record KnowledgeSnapshotLineageEntry(
        String lineageId,
        String sourceKnowledgeId,
        String targetKnowledgeId,
        String relationType,
        String createdBy,
        Instant createdAt,
        boolean requiredForReplay,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotLineageEntry {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
