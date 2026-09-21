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


import java.time.Duration;
import java.util.Objects;

/**
 * Represents a knowledge answer resolution freshness policy.
 *
 * <p>Its components capture `lease duration`, `grace period`, `heartbeat timeout`, `maximum clock skew`, `minimum live participants`, and other values.</p>
 *
 * @param leaseDuration the lease duration
 * @param gracePeriod the grace period
 * @param heartbeatTimeout the heartbeat timeout
 * @param maximumClockSkew the maximum clock skew
 * @param minimumLiveParticipants the minimum live participants
 * @param requireParticipantLiveness the require participant liveness
 * @param requireLease the require lease
 * @param allowGracePeriod the allow grace period
 * @param allowOfflineGrace the allow offline grace
 */


public record KnowledgeAnswerResolutionFreshnessPolicy(
        Duration leaseDuration,
        Duration gracePeriod,
        Duration heartbeatTimeout,
        Duration maximumClockSkew,
        int minimumLiveParticipants,
        boolean requireParticipantLiveness,
        boolean requireLease,
        boolean allowGracePeriod,
        boolean allowOfflineGrace
) {

    public KnowledgeAnswerResolutionFreshnessPolicy {
        Objects.requireNonNull(leaseDuration, "leaseDuration");
        Objects.requireNonNull(gracePeriod, "gracePeriod");
        Objects.requireNonNull(heartbeatTimeout, "heartbeatTimeout");
        Objects.requireNonNull(maximumClockSkew, "maximumClockSkew");

        if (leaseDuration.isZero() || leaseDuration.isNegative()) {
            throw new IllegalArgumentException(
                    "leaseDuration must be positive");
        }

        if (gracePeriod.isNegative()) {
            throw new IllegalArgumentException(
                    "gracePeriod must not be negative");
        }

        if (heartbeatTimeout.isZero() || heartbeatTimeout.isNegative()) {
            throw new IllegalArgumentException(
                    "heartbeatTimeout must be positive");
        }

        if (minimumLiveParticipants < 0) {
            throw new IllegalArgumentException(
                    "minimumLiveParticipants must be >= 0");
        }
    }

    public static KnowledgeAnswerResolutionFreshnessPolicy defaults() {
        return new KnowledgeAnswerResolutionFreshnessPolicy(
                Duration.ofMinutes(15),
                Duration.ofMinutes(5),
                Duration.ofMinutes(2),
                Duration.ofSeconds(30),
                1,
                true,
                true,
                true,
                false
        );
    }
}
