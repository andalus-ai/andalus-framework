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
 * Represents a knowledge evidence exchange protocol authentication message.
 *
 * <p>Its components capture `message id`, `correlation id`, `runtime id`, `identity fingerprint`, `key id`, and other values.</p>
 *
 * @param messageId the message id
 * @param correlationId the correlation id
 * @param runtimeId the runtime id
 * @param identityFingerprint the identity fingerprint
 * @param keyId the key id
 * @param keyVersion the key version
 * @param algorithm the algorithm
 * @param authentication the authentication
 * @param signedPayloadFingerprint the signed payload fingerprint
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeProtocolAuthenticationMessage(

        String messageId,

        String correlationId,

        String runtimeId,

        String identityFingerprint,

        String keyId,

        String keyVersion,

        KnowledgeEvidenceExchangeMessageAuthenticationAlgorithm
                algorithm,

        byte[] authentication,

        String signedPayloadFingerprint,

        Instant issuedAt,

        Instant expiresAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeProtocolAuthenticationMessage {

        authentication =
                authentication == null
                        ? new byte[0]
                        : authentication.clone();

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    @Override
    public byte[] authentication() {
        return authentication.clone();
    }
}
