package tech.kayys.wayang.knowledge.seal;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge snapshot secure seal.
 *
 * <p>Its components capture `seal id`, `snapshot id`, `snapshot fingerprint`, `algorithm`, `anchor type`, and other values.</p>
 *
 * @param sealId the seal id
 * @param snapshotId the snapshot id
 * @param snapshotFingerprint the snapshot fingerprint
 * @param algorithm the algorithm
 * @param anchorType the anchor type
 * @param keyId the key id
 * @param keyVersion the key version
 * @param signature the signature
 * @param createdAt the created at
 * @param expiresAt the expires at
 * @param status the status
 * @param metadata the metadata
 */


public record KnowledgeSnapshotSecureSeal(
        String sealId,
        KnowledgeSnapshotId snapshotId,
        String snapshotFingerprint,
        KnowledgeSnapshotSealAlgorithm algorithm,
        KnowledgeSnapshotTrustAnchorType anchorType,
        String keyId,
        String keyVersion,
        String signature,
        Instant createdAt,
        Instant expiresAt,
        KnowledgeSnapshotSealStatus status,
        Map<String, String> metadata
) {

    public KnowledgeSnapshotSecureSeal {
        Objects.requireNonNull(sealId, "sealId");
        Objects.requireNonNull(snapshotId, "snapshotId");
        Objects.requireNonNull(snapshotFingerprint, "snapshotFingerprint");
        Objects.requireNonNull(algorithm, "algorithm");
        Objects.requireNonNull(anchorType, "anchorType");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(status, "status");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean isExpired(Instant now) {
        return expiresAt != null && !expiresAt.isAfter(now);
    }
}
