package tech.kayys.wayang.knowledge.snapshot.pack;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge snapshot seal manifest.
 *
 * <p>Its components capture `seal id`, `algorithm`, `anchor type`, `key id`, `key version`, and other values.</p>
 *
 * @param sealId the seal id
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


public record KnowledgeSnapshotSealManifest(
        String sealId,
        String algorithm,
        String anchorType,
        String keyId,
        String keyVersion,
        String signature,
        Instant createdAt,
        Instant expiresAt,
        String status,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotSealManifest {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
