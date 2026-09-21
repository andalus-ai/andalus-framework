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


import java.io.OutputStream;

/**
 * Provides knowledge evidence exchange frame writer behavior for the Wayang framework.
 */


public final class KnowledgeEvidenceExchangeFrameWriter {

    private final KnowledgeEvidenceExchangeFrameCodec codec;

    public KnowledgeEvidenceExchangeFrameWriter(
            KnowledgeEvidenceExchangeFrameCodec codec
    ) {
        this.codec = codec;
    }

    public synchronized void write(
            KnowledgeEvidenceExchangeFrame frame,
            OutputStream output
    ) {

        codec.encode(
                frame,
                output
        );
    }
}
