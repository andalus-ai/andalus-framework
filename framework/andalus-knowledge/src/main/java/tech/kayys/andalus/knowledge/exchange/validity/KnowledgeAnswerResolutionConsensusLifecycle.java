package tech.kayys.andalus.knowledge.exchange.validity;

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
import java.util.Map;

/**
 * Represents a knowledge answer resolution consensus lifecycle.
 *
 * <p>Its components capture `consensus id`, `key fingerprint`, `state`, `revocation reason`, `superseded by consensus id`, and other values.</p>
 *
 * @param consensusId the consensus id
 * @param keyFingerprint the key fingerprint
 * @param state the state
 * @param revocationReason the revocation reason
 * @param supersededByConsensusId the superseded by consensus id
 * @param effectiveFrom the effective from
 * @param effectiveUntil the effective until
 * @param revokedAt the revoked at
 * @param archivedAt the archived at
 * @param actorRuntimeId the actor runtime id
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionConsensusLifecycle(

        String consensusId,

        String keyFingerprint,

        KnowledgeAnswerResolutionConsensusLifecycleState state,

        KnowledgeAnswerResolutionConsensusRevocationReason
                revocationReason,

        String supersededByConsensusId,

        Instant effectiveFrom,

        Instant effectiveUntil,

        Instant revokedAt,

        Instant archivedAt,

        String actorRuntimeId,

        Map<String, String> metadata
) {

    public KnowledgeAnswerResolutionConsensusLifecycle {

        metadata =
                metadata == null
                        ? Map.of()
                        : Map.copyOf(metadata);
    }

    public boolean activeAt(Instant time) {

        if (state !=
                KnowledgeAnswerResolutionConsensusLifecycleState.ACTIVE) {

            return false;
        }

        if (effectiveFrom != null
                && time.isBefore(effectiveFrom)) {

            return false;
        }

        return effectiveUntil == null
                || time.isBefore(effectiveUntil);
    }
}
