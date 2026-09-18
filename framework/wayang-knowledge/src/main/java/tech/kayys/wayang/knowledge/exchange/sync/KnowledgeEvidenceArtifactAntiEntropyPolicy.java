package tech.kayys.wayang.knowledge.exchange.sync;

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
import tech.kayys.wayang.knowledge.exchange.transfer.*;
import tech.kayys.wayang.knowledge.exchange.replication.*;
import tech.kayys.wayang.knowledge.exchange.sync.*;
import tech.kayys.wayang.knowledge.exchange.federation.*;
import tech.kayys.wayang.knowledge.exchange.routing.*;
import tech.kayys.wayang.knowledge.exchange.fusion.*;


import java.time.Duration;

/**
 * Represents a knowledge evidence artifact anti entropy policy.
 *
 * <p>Its components capture `interval`, `timeout`, `max artifacts per round`, `repair missing`, `verify divergent`, and other values.</p>
 *
 * @param interval the interval
 * @param timeout the timeout
 * @param maxArtifactsPerRound the max artifacts per round
 * @param repairMissing the repair missing
 * @param verifyDivergent the verify divergent
 * @param propagateRevocations the propagate revocations
 * @param requireAuthorization the require authorization
 */


public record KnowledgeEvidenceArtifactAntiEntropyPolicy(

        Duration interval,

        Duration timeout,

        int maxArtifactsPerRound,

        boolean repairMissing,

        boolean verifyDivergent,

        boolean propagateRevocations,

        boolean requireAuthorization

) {

    public static KnowledgeEvidenceArtifactAntiEntropyPolicy defaults() {

        return new KnowledgeEvidenceArtifactAntiEntropyPolicy(
                Duration.ofMinutes(5),
                Duration.ofMinutes(2),
                10_000,
                true,
                true,
                true,
                true
        );
    }
}
