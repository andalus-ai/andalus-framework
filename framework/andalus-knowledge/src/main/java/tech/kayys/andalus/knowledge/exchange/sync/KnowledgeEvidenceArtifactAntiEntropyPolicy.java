package tech.kayys.andalus.knowledge.exchange.sync;

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
import tech.kayys.andalus.knowledge.exchange.transfer.*;
import tech.kayys.andalus.knowledge.exchange.replication.*;
import tech.kayys.andalus.knowledge.exchange.sync.*;
import tech.kayys.andalus.knowledge.exchange.federation.*;
import tech.kayys.andalus.knowledge.exchange.routing.*;
import tech.kayys.andalus.knowledge.exchange.fusion.*;


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
