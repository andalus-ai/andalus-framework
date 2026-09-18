package tech.kayys.wayang.knowledge.snapshot.pack;

import tech.kayys.wayang.knowledge.integrity.KnowledgeSnapshotIntegrityMismatch;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge snapshot integrity manifest.
 *
 * <p>Its components capture `status`, `verifier id`, `verifier version`, `verified at`, `mismatches`, and other values.</p>
 *
 * @param status the status
 * @param verifierId the verifier id
 * @param verifierVersion the verifier version
 * @param verifiedAt the verified at
 * @param mismatches the mismatches
 * @param aggregateFingerprint the aggregate fingerprint
 * @param metadata the metadata
 */


public record KnowledgeSnapshotIntegrityManifest(
        String status,
        String verifierId,
        String verifierVersion,
        Instant verifiedAt,
        List<KnowledgeSnapshotIntegrityMismatch> mismatches,
        String aggregateFingerprint,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotIntegrityManifest {
        mismatches = mismatches == null ? List.of() : List.copyOf(mismatches);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
