package tech.kayys.wayang.knowledge.integrity;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge attestation.
 *
 * <p>Its components capture `attestation id`, `snapshot id`, `statement`, `attester id`, `attester type`, and other values.</p>
 *
 * @param attestationId the attestation id
 * @param snapshotId the snapshot id
 * @param statement the statement
 * @param attesterId the attester id
 * @param attesterType the attester type
 * @param signature the signature
 * @param attestedAt the attested at
 * @param metadata the metadata
 */


public record KnowledgeAttestation(
        String attestationId,
        KnowledgeSnapshotId snapshotId,
        String statement,
        String attesterId,
        String attesterType,
        String signature,
        Instant attestedAt,
        Map<String, Object> metadata
) {

    public KnowledgeAttestation {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
