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
 * Represents a knowledge evidence exchange protocol negotiation message.
 *
 * <p>Its components capture `message id`, `correlation id`, `result`, `local manifest fingerprint`, `remote manifest fingerprint`, and other values.</p>
 *
 * @param messageId the message id
 * @param correlationId the correlation id
 * @param result the result
 * @param localManifestFingerprint the local manifest fingerprint
 * @param remoteManifestFingerprint the remote manifest fingerprint
 * @param selectedProtocolVersion the selected protocol version
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeProtocolNegotiationMessage(

        String messageId,

        String correlationId,

        KnowledgeEvidenceExchangeCapabilityNegotiationResult
                result,

        String localManifestFingerprint,

        String remoteManifestFingerprint,

        KnowledgeEvidenceExchangeProtocolVersion
                selectedProtocolVersion,

        Instant issuedAt,

        Instant expiresAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeProtocolNegotiationMessage {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
