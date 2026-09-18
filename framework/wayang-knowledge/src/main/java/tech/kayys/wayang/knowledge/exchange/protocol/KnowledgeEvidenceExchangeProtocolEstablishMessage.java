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
 * Represents a knowledge evidence exchange protocol establish message.
 *
 * <p>Its components capture `message id`, `correlation id`, `session id`, `handshake id`, `local runtime id`, and other values.</p>
 *
 * @param messageId the message id
 * @param correlationId the correlation id
 * @param sessionId the session id
 * @param handshakeId the handshake id
 * @param localRuntimeId the local runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param negotiatedProtocolVersion the negotiated protocol version
 * @param capabilityFingerprint the capability fingerprint
 * @param sessionFingerprint the session fingerprint
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeProtocolEstablishMessage(

        String messageId,

        String correlationId,

        String sessionId,

        String handshakeId,

        String localRuntimeId,

        String remoteRuntimeId,

        String negotiatedProtocolVersion,

        String capabilityFingerprint,

        String sessionFingerprint,

        Instant issuedAt,

        Instant expiresAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeProtocolEstablishMessage {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
