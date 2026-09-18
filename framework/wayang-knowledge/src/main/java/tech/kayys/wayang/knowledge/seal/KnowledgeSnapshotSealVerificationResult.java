package tech.kayys.wayang.knowledge.seal;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.time.Instant;
import java.util.List;

/**
 * Represents a knowledge snapshot seal verification result.
 *
 * <p>Its components capture `verification id`, `snapshot id`, `status`, `key id`, `key version`, and other values.</p>
 *
 * @param verificationId the verification id
 * @param snapshotId the snapshot id
 * @param status the status
 * @param keyId the key id
 * @param keyVersion the key version
 * @param verifiedAt the verified at
 * @param diagnostics the diagnostics
 */


public record KnowledgeSnapshotSealVerificationResult(
        String verificationId,
        KnowledgeSnapshotId snapshotId,
        KnowledgeSnapshotSealStatus status,
        String keyId,
        String keyVersion,
        Instant verifiedAt,
        List<String> diagnostics
) {

    public KnowledgeSnapshotSealVerificationResult {
        diagnostics = diagnostics == null ? List.of() : List.copyOf(diagnostics);
    }

    public boolean valid() {
        return status == KnowledgeSnapshotSealStatus.VERIFIED;
    }
}
