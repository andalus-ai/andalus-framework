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
import java.util.Objects;

/**
 * Represents a knowledge answer resolution lease.
 *
 * <p>Its components capture `lease id`, `consensus id`, `key fingerprint`, `runtime id`, `issued at`, and other values.</p>
 *
 * @param leaseId the lease id
 * @param consensusId the consensus id
 * @param keyFingerprint the key fingerprint
 * @param runtimeId the runtime id
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param lastRenewedAt the last renewed at
 * @param renewalSequence the renewal sequence
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionLease(
        String leaseId,
        String consensusId,
        String keyFingerprint,
        String runtimeId,
        Instant issuedAt,
        Instant expiresAt,
        Instant lastRenewedAt,
        long renewalSequence,
        String metadata
) {

    public KnowledgeAnswerResolutionLease {
        Objects.requireNonNull(leaseId, "leaseId");
        Objects.requireNonNull(consensusId, "consensusId");
        Objects.requireNonNull(keyFingerprint, "keyFingerprint");
        Objects.requireNonNull(runtimeId, "runtimeId");
        Objects.requireNonNull(issuedAt, "issuedAt");
        Objects.requireNonNull(expiresAt, "expiresAt");

        if (!expiresAt.isAfter(issuedAt)) {
            throw new IllegalArgumentException(
                    "expiresAt must be after issuedAt");
        }

        if (renewalSequence < 0) {
            throw new IllegalArgumentException(
                    "renewalSequence must be >= 0");
        }
    }

    public boolean activeAt(Instant instant) {
        Objects.requireNonNull(instant, "instant");

        return !instant.isBefore(issuedAt)
                && instant.isBefore(expiresAt);
    }

    public boolean expiredAt(Instant instant) {
        return !activeAt(instant);
    }
}
