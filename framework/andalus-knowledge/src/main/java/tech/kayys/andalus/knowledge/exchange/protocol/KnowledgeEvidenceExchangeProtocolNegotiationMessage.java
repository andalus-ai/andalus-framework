package tech.kayys.andalus.knowledge.exchange.protocol;

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
