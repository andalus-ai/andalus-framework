package tech.kayys.andalus.knowledge.exchange.attestation;

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
import java.util.Map;

/**
 * Represents a knowledge answer resolution consensus result.
 *
 * <p>Its components capture `consensus id`, `key fingerprint`, `status`, `winning resolution fingerprint`, `winning dependency fingerprint`, and other values.</p>
 *
 * @param consensusId the consensus id
 * @param keyFingerprint the key fingerprint
 * @param status the status
 * @param winningResolutionFingerprint the winning resolution fingerprint
 * @param winningDependencyFingerprint the winning dependency fingerprint
 * @param eligibleParticipants the eligible participants
 * @param receivedVotes the received votes
 * @param agreeingVotes the agreeing votes
 * @param requiredVotes the required votes
 * @param agreementRatio the agreement ratio
 * @param votes the votes
 * @param diagnostics the diagnostics
 * @param decidedAt the decided at
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionConsensusResult(

        String consensusId,

        String keyFingerprint,

        KnowledgeAnswerResolutionConsensusStatus status,

        String winningResolutionFingerprint,

        String winningDependencyFingerprint,

        int eligibleParticipants,

        int receivedVotes,

        int agreeingVotes,

        int requiredVotes,

        double agreementRatio,

        List<KnowledgeAnswerResolutionVote> votes,

        List<String> diagnostics,

        Instant decidedAt,

        Map<String, String> metadata
) {

    public KnowledgeAnswerResolutionConsensusResult {

        votes =
                votes == null
                        ? List.of()
                        : List.copyOf(votes);

        diagnostics =
                diagnostics == null
                        ? List.of()
                        : List.copyOf(diagnostics);

        metadata =
                metadata == null
                        ? Map.of()
                        : Map.copyOf(metadata);
    }
}
