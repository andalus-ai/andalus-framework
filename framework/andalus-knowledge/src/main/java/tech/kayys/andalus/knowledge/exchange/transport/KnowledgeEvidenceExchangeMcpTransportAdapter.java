package tech.kayys.andalus.knowledge.exchange.transport;

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


import java.util.concurrent.CompletableFuture;

/**
 * Provides knowledge evidence exchange mcp transport adapter behavior for the Wayang framework.
 */


public final class KnowledgeEvidenceExchangeMcpTransportAdapter {

    private final KnowledgeEvidenceExchangeMultiplexer multiplexer;

    public KnowledgeEvidenceExchangeMcpTransportAdapter(
            KnowledgeEvidenceExchangeMultiplexer multiplexer
    ) {
        this.multiplexer = multiplexer;
    }

    public CompletableFuture<
            KnowledgeEvidenceExchangeTransportResponse
            > exchange(
                    KnowledgeEvidenceExchangeProtocolSession session,
                    KnowledgeEvidenceExchangeTransportRequest request
            ) {

        var stream =
                multiplexer.openStream(
                        session.sessionId()
                );

        return stream.send(request);
    }
}
