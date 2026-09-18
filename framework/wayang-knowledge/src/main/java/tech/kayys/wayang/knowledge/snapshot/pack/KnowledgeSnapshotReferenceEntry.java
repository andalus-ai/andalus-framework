package tech.kayys.wayang.knowledge.snapshot.pack;

import java.util.Map;

/**
 * Represents a knowledge snapshot reference entry.
 *
 * <p>Its components capture `id`, `version id`, `kind`, `fingerprint`, `required for replay`, and other values.</p>
 *
 * @param id the id
 * @param versionId the version id
 * @param kind the kind
 * @param fingerprint the fingerprint
 * @param requiredForReplay the required for replay
 * @param requiredForAudit the required for audit
 * @param metadata the metadata
 */


public record KnowledgeSnapshotReferenceEntry(
        String id,
        String versionId,
        String kind,
        String fingerprint,
        boolean requiredForReplay,
        boolean requiredForAudit,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotReferenceEntry {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
