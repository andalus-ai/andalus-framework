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
import java.util.Objects;

/**
 * Represents a knowledge evidence exchange protocol envelope.
 *
 * <p>Its components capture `message id`, `type`, `protocol version`, `sender runtime id`, `receiver runtime id`, and other values.</p>
 *
 * @param messageId the message id
 * @param type the type
 * @param protocolVersion the protocol version
 * @param senderRuntimeId the sender runtime id
 * @param receiverRuntimeId the receiver runtime id
 * @param sessionId the session id
 * @param correlationId the correlation id
 * @param nonce the nonce
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param payloadFingerprint the payload fingerprint
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeProtocolEnvelope(

        String messageId,

        KnowledgeEvidenceExchangeProtocolMessageType type,

        String protocolVersion,

        String senderRuntimeId,

        String receiverRuntimeId,

        String sessionId,

        String correlationId,

        String nonce,

        Instant issuedAt,

        Instant expiresAt,

        String payloadFingerprint,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeProtocolEnvelope {
        Objects.requireNonNull(messageId);
        Objects.requireNonNull(type);
        Objects.requireNonNull(protocolVersion);
        Objects.requireNonNull(senderRuntimeId);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean activeAt(Instant at) {

        if (issuedAt != null &&
                at.isBefore(issuedAt)) {
            return false;
        }

        return expiresAt == null ||
                at.isBefore(expiresAt);
    }
}
