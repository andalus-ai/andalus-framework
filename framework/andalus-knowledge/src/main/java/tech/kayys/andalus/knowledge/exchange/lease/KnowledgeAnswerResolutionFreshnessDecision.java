package tech.kayys.andalus.knowledge.exchange.lease;

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
import tech.kayys.andalus.knowledge.exchange.coverage.*;
import tech.kayys.andalus.knowledge.exchange.gap.*;
import tech.kayys.andalus.knowledge.exchange.attribution.*;
import tech.kayys.andalus.knowledge.exchange.contradiction.*;
import tech.kayys.andalus.knowledge.exchange.factuality.*;
import tech.kayys.andalus.knowledge.exchange.uncertainty.*;
import tech.kayys.andalus.knowledge.exchange.compact.*;
import tech.kayys.andalus.knowledge.exchange.resolution.*;
import tech.kayys.andalus.knowledge.exchange.quorum.*;
import tech.kayys.andalus.knowledge.exchange.selection.*;
import tech.kayys.andalus.knowledge.exchange.coordination.*;
import tech.kayys.andalus.knowledge.exchange.attestation.*;
import tech.kayys.andalus.knowledge.exchange.proof.*;
import tech.kayys.andalus.knowledge.exchange.validity.*;
import tech.kayys.andalus.knowledge.exchange.lease.*;
import tech.kayys.andalus.knowledge.exchange.recovery.*;


import java.time.Instant;
import java.util.List;

/**
 * Represents a knowledge answer resolution freshness decision.
 *
 * <p>Its components capture `status`, `liveness`, `consensus id`, `key fingerprint`, `participant count`, and other values.</p>
 *
 * @param status the status
 * @param liveness the liveness
 * @param consensusId the consensus id
 * @param keyFingerprint the key fingerprint
 * @param participantCount the participant count
 * @param liveParticipantCount the live participant count
 * @param requiredLiveParticipants the required live participants
 * @param evaluatedAt the evaluated at
 * @param leaseExpiresAt the lease expires at
 * @param graceExpiresAt the grace expires at
 * @param liveRuntimeIds the live runtime ids
 * @param diagnostics the diagnostics
 */


public record KnowledgeAnswerResolutionFreshnessDecision(
        KnowledgeAnswerResolutionFreshnessStatus status,
        KnowledgeAnswerResolutionLivenessStatus liveness,
        String consensusId,
        String keyFingerprint,
        int participantCount,
        int liveParticipantCount,
        int requiredLiveParticipants,
        Instant evaluatedAt,
        Instant leaseExpiresAt,
        Instant graceExpiresAt,
        List<String> liveRuntimeIds,
        List<String> diagnostics
) {

    public KnowledgeAnswerResolutionFreshnessDecision {
        liveRuntimeIds = liveRuntimeIds == null
                ? List.of()
                : List.copyOf(liveRuntimeIds);

        diagnostics = diagnostics == null
                ? List.of()
                : List.copyOf(diagnostics);
    }

    public boolean usable() {
        return status == KnowledgeAnswerResolutionFreshnessStatus.FRESH
                || status == KnowledgeAnswerResolutionFreshnessStatus.GRACE_PERIOD;
    }
}
