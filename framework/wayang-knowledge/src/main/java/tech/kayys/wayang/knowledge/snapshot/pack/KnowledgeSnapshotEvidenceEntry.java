package tech.kayys.wayang.knowledge.snapshot.pack;

import java.util.Map;

/**
 * Represents a knowledge snapshot evidence entry.
 *
 * <p>Its components capture `knowledge id`, `version id`, `fingerprint`, `provenance id`, `authority fingerprint`, and other values.</p>
 *
 * @param knowledgeId the knowledge id
 * @param versionId the version id
 * @param fingerprint the fingerprint
 * @param provenanceId the provenance id
 * @param authorityFingerprint the authority fingerprint
 * @param trustFingerprint the trust fingerprint
 * @param lineageFingerprint the lineage fingerprint
 * @param requiredForReplay the required for replay
 * @param requiredForAudit the required for audit
 * @param metadata the metadata
 */


public record KnowledgeSnapshotEvidenceEntry(
        String knowledgeId,
        String versionId,
        String fingerprint,
        String provenanceId,
        String authorityFingerprint,
        String trustFingerprint,
        String lineageFingerprint,
        boolean requiredForReplay,
        boolean requiredForAudit,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotEvidenceEntry {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
