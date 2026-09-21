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
import java.util.Objects;

/**
 * Represents a knowledge answer resolution consensus epoch.
 *
 * <p>Its components capture `epoch id`, `sequence`, `participant set fingerprint`, `previous epoch id`, `created at`, and other values.</p>
 *
 * @param epochId the epoch id
 * @param sequence the sequence
 * @param participantSetFingerprint the participant set fingerprint
 * @param previousEpochId the previous epoch id
 * @param createdAt the created at
 * @param effectiveAt the effective at
 * @param expiresAt the expires at
 */


public record KnowledgeAnswerResolutionConsensusEpoch(
        String epochId,
        long sequence,
        String participantSetFingerprint,
        String previousEpochId,
        Instant createdAt,
        Instant effectiveAt,
        Instant expiresAt
) {
    public KnowledgeAnswerResolutionConsensusEpoch(
            String epochId,
            long sequence,
            String participantSetFingerprint,
            Instant createdAt,
            Instant expiresAt) {
        this(epochId, sequence, participantSetFingerprint, null, createdAt, createdAt, expiresAt);
    }

    public KnowledgeAnswerResolutionConsensusEpoch {
        Objects.requireNonNull(epochId, "epochId");
        Objects.requireNonNull(participantSetFingerprint, "participantSetFingerprint");
        Objects.requireNonNull(createdAt, "createdAt");
        effectiveAt = effectiveAt != null ? effectiveAt : createdAt;

        if (sequence < 0) {
            throw new IllegalArgumentException("sequence must be >= 0");
        }
    }

    public boolean effectiveAt(Instant instant) {
        return !instant.isBefore(effectiveAt)
                && (expiresAt == null || instant.isBefore(expiresAt));
    }
}
