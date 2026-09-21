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


import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

/**
 * Defines the contract for knowledge evidence exchange transport connection operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeTransportConnection
        extends AutoCloseable {

    String connectionId();

    KnowledgeEvidenceExchangeTransportDescriptor descriptor();

    KnowledgeEvidenceExchangeTransportState state();

    CompletableFuture<
            KnowledgeEvidenceExchangeTransportResponse
            > send(
                    KnowledgeEvidenceExchangeTransportRequest request
            );

    Flow.Publisher<KnowledgeEvidenceExchangeTransportMessage>
    incoming();

    void cancel(String requestId);

    void close();

    default boolean activeAt(Instant now) {
        return state() ==
                KnowledgeEvidenceExchangeTransportState.CONNECTED;
    }
}
