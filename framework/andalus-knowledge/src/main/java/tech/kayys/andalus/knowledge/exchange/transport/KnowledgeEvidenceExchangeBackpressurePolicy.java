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
