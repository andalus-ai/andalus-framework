package tech.kayys.wayang.knowledge.exchange.protocol;

import tech.kayys.wayang.knowledge.*;
import tech.kayys.wayang.knowledge.seal.*;
import tech.kayys.wayang.knowledge.snapshot.*;
import tech.kayys.wayang.knowledge.snapshot.pack.*;
import tech.kayys.wayang.knowledge.snapshot.artifact.*;
import tech.kayys.wayang.knowledge.snapshot.merkle.*;
import tech.kayys.wayang.knowledge.exchange.*;
import tech.kayys.wayang.knowledge.exchange.auth.*;
import tech.kayys.wayang.knowledge.exchange.session.*;
import tech.kayys.wayang.knowledge.exchange.binding.*;
import tech.kayys.wayang.knowledge.exchange.envelope.*;
import tech.kayys.wayang.knowledge.exchange.trust.*;
import tech.kayys.wayang.knowledge.exchange.identity.*;
import tech.kayys.wayang.knowledge.exchange.capability.*;
import tech.kayys.wayang.knowledge.exchange.protocol.*;
import tech.kayys.wayang.knowledge.exchange.transport.*;
import tech.kayys.wayang.knowledge.exchange.framing.*;


import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence exchange protocol capabilities message.
 *
 * <p>Its components capture `message id`, `correlation id`, `manifest`, `manifest fingerprint`, `issued at`, and other values.</p>
 *
 * @param messageId the message id
 * @param correlationId the correlation id
 * @param manifest the manifest
 * @param manifestFingerprint the manifest fingerprint
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeProtocolCapabilitiesMessage(

        String messageId,

        String correlationId,

        KnowledgeEvidenceExchangeCapabilityManifest manifest,

        String manifestFingerprint,

        Instant issuedAt,

        Instant expiresAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeProtocolCapabilitiesMessage {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
