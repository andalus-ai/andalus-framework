package tech.kayys.wayang.knowledge.seal;

import java.time.Instant;

/**
 * Represents a knowledge snapshot seal payload.
 *
 * <p>Its components capture `snapshot id`, `snapshot fingerprint`, `integrity status`, `verifier id`, `verifier version`, and other values.</p>
 *
 * @param snapshotId the snapshot id
 * @param snapshotFingerprint the snapshot fingerprint
 * @param integrityStatus the integrity status
 * @param verifierId the verifier id
 * @param verifierVersion the verifier version
 * @param verifiedAt the verified at
 */


public record KnowledgeSnapshotSealPayload(
        String snapshotId,
        String snapshotFingerprint,
        String integrityStatus,
        String verifierId,
        String verifierVersion,
        Instant verifiedAt
) {}
