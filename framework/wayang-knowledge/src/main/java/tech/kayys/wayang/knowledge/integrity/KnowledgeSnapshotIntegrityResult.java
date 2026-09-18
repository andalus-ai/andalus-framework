package tech.kayys.wayang.knowledge.integrity;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge snapshot integrity result.
 *
 * <p>Its components capture `verification id`, `snapshot id`, `status`, `verified at`, `mismatches`, and other values.</p>
 *
 * @param verificationId the verification id
 * @param snapshotId the snapshot id
 * @param status the status
 * @param verifiedAt the verified at
 * @param mismatches the mismatches
 * @param verifiedDependencies the verified dependencies
 * @param missingDependencies the missing dependencies
 * @param computedFingerprint the computed fingerprint
 * @param expectedFingerprint the expected fingerprint
 * @param metadata the metadata
 */


public record KnowledgeSnapshotIntegrityResult(
        String verificationId,
        KnowledgeSnapshotId snapshotId,
        KnowledgeSnapshotIntegrityStatus status,
        Instant verifiedAt,
        List<KnowledgeSnapshotIntegrityMismatch> mismatches,
        List<String> verifiedDependencies,
        List<String> missingDependencies,
        String computedFingerprint,
        String expectedFingerprint,
        Map<String, String> metadata
) {

    public KnowledgeSnapshotIntegrityResult {
        mismatches = mismatches == null ? List.of() : List.copyOf(mismatches);
        verifiedDependencies = verifiedDependencies == null ? List.of() : List.copyOf(verifiedDependencies);
        missingDependencies = missingDependencies == null ? List.of() : List.copyOf(missingDependencies);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean isValid() {
        return status == KnowledgeSnapshotIntegrityStatus.ATTESTED;
    }
}
