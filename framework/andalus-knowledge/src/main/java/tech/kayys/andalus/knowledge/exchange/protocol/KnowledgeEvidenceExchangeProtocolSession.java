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
 * Represents a knowledge evidence exchange protocol session.
 *
 * <p>Its components capture `session id`, `local runtime id`, `remote runtime id`, `state`, `negotiated protocol version`, and other values.</p>
 *
 * @param sessionId the session id
 * @param localRuntimeId the local runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param state the state
 * @param negotiatedProtocolVersion the negotiated protocol version
 * @param localNonce the local nonce
 * @param remoteNonce the remote nonce
 * @param handshakeId the handshake id
 * @param localIdentityFingerprint the local identity fingerprint
 * @param remoteIdentityFingerprint the remote identity fingerprint
 * @param localCapabilityFingerprint the local capability fingerprint
 * @param remoteCapabilityFingerprint the remote capability fingerprint
 * @param sessionFingerprint the session fingerprint
 * @param createdAt the created at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeProtocolSession(

        String sessionId,

        String localRuntimeId,

        String remoteRuntimeId,

        KnowledgeEvidenceExchangeProtocolState state,

        KnowledgeEvidenceExchangeProtocolVersion
                negotiatedProtocolVersion,

        String localNonce,

        String remoteNonce,

        String handshakeId,

        String localIdentityFingerprint,

        String remoteIdentityFingerprint,

        String localCapabilityFingerprint,

        String remoteCapabilityFingerprint,

        String sessionFingerprint,

        Instant createdAt,

        Instant expiresAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeProtocolSession {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean activeAt(Instant at) {

        return state ==
                KnowledgeEvidenceExchangeProtocolState.ESTABLISHED ||
                state ==
                KnowledgeEvidenceExchangeProtocolState.EXCHANGING
                &&
                (expiresAt == null ||
                        at.isBefore(expiresAt));
    }
}
