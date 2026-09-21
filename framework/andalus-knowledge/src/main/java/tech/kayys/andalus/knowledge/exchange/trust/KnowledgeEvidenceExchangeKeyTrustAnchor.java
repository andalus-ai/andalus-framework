package tech.kayys.andalus.knowledge.exchange.trust;

import tech.kayys.andalus.knowledge.*;
import tech.kayys.andalus.knowledge.seal.*;
import tech.kayys.andalus.knowledge.snapshot.*;
import tech.kayys.andalus.knowledge.snapshot.pack.*;
import tech.kayys.andalus.knowledge.snapshot.artifact.*;
import tech.kayys.andalus.knowledge.snapshot.merkle.*;
import tech.kayys.andalus.knowledge.exchange.*;
import tech.kayys.andalus.knowledge.exchange.auth.*;
import tech.kayys.andalus.knowledge.exchange.session.*;
import tech.kayys.andalus.knowledge.exchange.binding.*;
import tech.kayys.andalus.knowledge.exchange.envelope.*;
import tech.kayys.andalus.knowledge.exchange.trust.*;
import tech.kayys.andalus.knowledge.exchange.identity.*;
import tech.kayys.andalus.knowledge.exchange.capability.*;
import tech.kayys.andalus.knowledge.exchange.protocol.*;
import tech.kayys.andalus.knowledge.exchange.transport.*;
import tech.kayys.andalus.knowledge.exchange.framing.*;


import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge evidence exchange key trust anchor.
 *
 * <p>Its components capture `anchor id`, `anchor type`, `runtime id`, `tenant id`, `authority`, and other values.</p>
 *
 * @param anchorId the anchor id
 * @param anchorType the anchor type
 * @param runtimeId the runtime id
 * @param tenantId the tenant id
 * @param authority the authority
 * @param createdAt the created at
 * @param expiresAt the expires at
 * @param trusted the trusted
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeKeyTrustAnchor(

        String anchorId,

        KnowledgeSnapshotTrustAnchorType anchorType,

        String runtimeId,

        String tenantId,

        String authority,

        Instant createdAt,

        Instant expiresAt,

        boolean trusted,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeKeyTrustAnchor {
        Objects.requireNonNull(anchorId, "anchorId");
        Objects.requireNonNull(anchorType, "anchorType");
        Objects.requireNonNull(createdAt, "createdAt");

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean activeAt(Instant at) {

        if (!trusted) {
            return false;
        }

        if (expiresAt != null &&
                at.isAfter(expiresAt)) {
            return false;
        }

        return !at.isBefore(createdAt);
    }
}
