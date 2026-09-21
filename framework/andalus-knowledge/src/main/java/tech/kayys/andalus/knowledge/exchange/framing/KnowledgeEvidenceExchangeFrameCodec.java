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


import java.io.InputStream;
import java.io.OutputStream;

/**
 * Defines the contract for knowledge evidence exchange frame codec operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeFrameCodec {

    byte[] encode(
            KnowledgeEvidenceExchangeFrame frame
    );

    KnowledgeEvidenceExchangeFrame decode(
            byte[] bytes
    );

    void encode(
            KnowledgeEvidenceExchangeFrame frame,
            OutputStream output
    );

    KnowledgeEvidenceExchangeFrame decode(
            InputStream input
    );
}
