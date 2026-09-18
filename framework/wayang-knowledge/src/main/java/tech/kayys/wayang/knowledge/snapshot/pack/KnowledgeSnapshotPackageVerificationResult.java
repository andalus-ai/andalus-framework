package tech.kayys.wayang.knowledge.snapshot.pack;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge snapshot package verification result.
 *
 * <p>Its components capture `package id`, `status`, `snapshot id`, `snapshot fingerprint`, `issues`, and other values.</p>
 *
 * @param packageId the package id
 * @param status the status
 * @param snapshotId the snapshot id
 * @param snapshotFingerprint the snapshot fingerprint
 * @param issues the issues
 * @param integrityVerified the integrity verified
 * @param sealVerified the seal verified
 * @param dependenciesVerified the dependencies verified
 * @param metadata the metadata
 */


public record KnowledgeSnapshotPackageVerificationResult(
        String packageId,
        KnowledgeSnapshotPackageVerificationStatus status,
        String snapshotId,
        String snapshotFingerprint,
        List<KnowledgeSnapshotPackageVerificationIssue> issues,
        boolean integrityVerified,
        boolean sealVerified,
        boolean dependenciesVerified,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotPackageVerificationResult {
        issues = issues == null ? List.of() : List.copyOf(issues);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean verified() {
        return status == KnowledgeSnapshotPackageVerificationStatus.VERIFIED;
    }
}
