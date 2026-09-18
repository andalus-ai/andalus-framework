package tech.kayys.wayang.knowledge.exchange.envelope;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence exchange key reference.
 *
 * <p>Its components capture `key id`, `key version`, `algorithm`, `runtime id`, `created at`, and other values.</p>
 *
 * @param keyId the key id
 * @param keyVersion the key version
 * @param algorithm the algorithm
 * @param runtimeId the runtime id
 * @param createdAt the created at
 * @param expiresAt the expires at
 * @param revoked the revoked
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeKeyReference(
        String keyId,
        String keyVersion,
        KnowledgeEvidenceExchangeMessageAuthenticationAlgorithm algorithm,
        String runtimeId,
        Instant createdAt,
        Instant expiresAt,
        boolean revoked,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceExchangeKeyReference {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean usableAt(Instant now) {
        if (revoked) {
            return false;
        }
        if (expiresAt != null && now.isAfter(expiresAt)) {
            return false;
        }
        return true;
    }
}
