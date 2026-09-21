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


import java.util.EnumSet;
import java.util.Set;

/**
 * Defines the knowledge evidence exchange frame flags values used by the Wayang framework.
 */


public enum KnowledgeEvidenceExchangeFrameFlags {

    FIRST,

    LAST,

    FIN,

    ACK_REQUIRED,

    COMPRESSED,

    ENCRYPTED,

    SIGNED,

    CHECKSUMMED,

    RESUMABLE,

    RETRANSMIT;

    public static Set<KnowledgeEvidenceExchangeFrameFlags> none() {
        return EnumSet.noneOf(
                KnowledgeEvidenceExchangeFrameFlags.class
        );
    }
}
