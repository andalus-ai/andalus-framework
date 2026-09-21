package tech.kayys.andalus.knowledge.exchange.framing;

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

/**
 * Provides knowledge evidence exchange wire stream behavior for the Wayang framework.
 */



public final class KnowledgeEvidenceExchangeWireStream {

    private final String streamId;

    private final String sessionId;

    private final KnowledgeEvidenceExchangeStreamSequence
            sequence =
            new KnowledgeEvidenceExchangeStreamSequence();

    private final KnowledgeEvidenceExchangeFlowControlWindow
            sendWindow;

    private final KnowledgeEvidenceExchangeFlowControlWindow
            receiveWindow;

    public KnowledgeEvidenceExchangeWireStream(
            String streamId,
            String sessionId,
            long initialWindowBytes
    ) {

        this.streamId = streamId;
        this.sessionId = sessionId;

        this.sendWindow =
                new KnowledgeEvidenceExchangeFlowControlWindow(
                        initialWindowBytes
                );

        this.receiveWindow =
                new KnowledgeEvidenceExchangeFlowControlWindow(
                        initialWindowBytes
                );
    }

    public String streamId() {
        return streamId;
    }

    public String sessionId() {
        return sessionId;
    }

    public long nextSequence() {
        return sequence.next();
    }

    public KnowledgeEvidenceExchangeFlowControlWindow
    sendWindow() {
        return sendWindow;
    }

    public KnowledgeEvidenceExchangeFlowControlWindow
    receiveWindow() {
        return receiveWindow;
    }
}
