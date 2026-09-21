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

/**
 * Defines the contract for knowledge evidence exchange secure handshake service operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeSecureHandshakeService {

    KnowledgeEvidenceExchangeProtocolSession initiate(
            KnowledgeEvidenceExchangeRuntimeIdentity localIdentity,
            KnowledgeEvidenceExchangeProtocolHello localHello,
            Instant now
    );

    KnowledgeEvidenceExchangeProtocolSession receiveHello(
            KnowledgeEvidenceExchangeProtocolSession session,
            KnowledgeEvidenceExchangeProtocolHello remoteHello,
            Instant now
    );

    KnowledgeEvidenceExchangeProtocolSession authenticate(
            KnowledgeEvidenceExchangeProtocolSession session,
            KnowledgeEvidenceExchangeProtocolAuthenticationMessage
                    authentication,
            Instant now
    );

    KnowledgeEvidenceExchangeProtocolSession negotiate(
            KnowledgeEvidenceExchangeProtocolSession session,
            KnowledgeEvidenceExchangeCapabilityNegotiationResult
                    negotiation,
            Instant now
    );

    KnowledgeEvidenceExchangeProtocolSession establish(
            KnowledgeEvidenceExchangeProtocolSession session,
            String handshakeId,
            Instant now
    );
}
