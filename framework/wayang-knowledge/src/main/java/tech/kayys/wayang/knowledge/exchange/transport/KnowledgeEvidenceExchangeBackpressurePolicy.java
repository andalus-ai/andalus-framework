package tech.kayys.wayang.knowledge.exchange.transport;

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

/**
 * Represents a knowledge evidence exchange backpressure policy.
 *
 * <p>Its components capture `max buffered messages`, `max buffered bytes`, `reject when full`, `block producer`.</p>
 *
 * @param maxBufferedMessages the max buffered messages
 * @param maxBufferedBytes the max buffered bytes
 * @param rejectWhenFull the reject when full
 * @param blockProducer the block producer
 */



public record KnowledgeEvidenceExchangeBackpressurePolicy(

        int maxBufferedMessages,

        long maxBufferedBytes,

        boolean rejectWhenFull,

        boolean blockProducer

) {

    public static KnowledgeEvidenceExchangeBackpressurePolicy
    conservative() {

        return new KnowledgeEvidenceExchangeBackpressurePolicy(
                256,
                16L * 1024L * 1024L,
                true,
                false
        );
    }
}
