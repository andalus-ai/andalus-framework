package tech.kayys.wayang.knowledge.seal;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge snapshot external trust anchor.
 *
 * <p>Its components capture `anchor id`, `snapshot id`, `type`, `external reference`, `anchored digest`, and other values.</p>
 *
 * @param anchorId the anchor id
 * @param snapshotId the snapshot id
 * @param type the type
 * @param externalReference the external reference
 * @param anchoredDigest the anchored digest
 * @param anchoredAt the anchored at
 * @param providerId the provider id
 * @param providerVersion the provider version
 * @param metadata the metadata
 */


public record KnowledgeSnapshotExternalTrustAnchor(
        String anchorId,
        KnowledgeSnapshotId snapshotId,
        KnowledgeSnapshotTrustAnchorType type,
        String externalReference,
        String anchoredDigest,
        Instant anchoredAt,
        String providerId,
        String providerVersion,
        Map<String, String> metadata
) {

    public KnowledgeSnapshotExternalTrustAnchor {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
