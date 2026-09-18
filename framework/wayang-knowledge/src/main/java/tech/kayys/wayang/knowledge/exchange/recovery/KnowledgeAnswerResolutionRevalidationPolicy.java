package tech.kayys.wayang.knowledge.exchange.recovery;

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
import tech.kayys.wayang.knowledge.exchange.coverage.*;
import tech.kayys.wayang.knowledge.exchange.gap.*;
import tech.kayys.wayang.knowledge.exchange.attribution.*;
import tech.kayys.wayang.knowledge.exchange.contradiction.*;
import tech.kayys.wayang.knowledge.exchange.factuality.*;
import tech.kayys.wayang.knowledge.exchange.uncertainty.*;
import tech.kayys.wayang.knowledge.exchange.compact.*;
import tech.kayys.wayang.knowledge.exchange.resolution.*;
import tech.kayys.wayang.knowledge.exchange.quorum.*;
import tech.kayys.wayang.knowledge.exchange.selection.*;
import tech.kayys.wayang.knowledge.exchange.coordination.*;
import tech.kayys.wayang.knowledge.exchange.attestation.*;
import tech.kayys.wayang.knowledge.exchange.proof.*;
import tech.kayys.wayang.knowledge.exchange.validity.*;
import tech.kayys.wayang.knowledge.exchange.lease.*;
import tech.kayys.wayang.knowledge.exchange.recovery.*;


import java.time.Duration;
import java.util.Objects;

/**
 * Represents a knowledge answer resolution revalidation policy.
 *
 * <p>Its components capture `revalidation window`, `renewal window`, `revalidate before use`, `allow grace period use`, `require dependency validation`, and other values.</p>
 *
 * @param revalidationWindow the revalidation window
 * @param renewalWindow the renewal window
 * @param revalidateBeforeUse the revalidate before use
 * @param allowGracePeriodUse the allow grace period use
 * @param requireDependencyValidation the require dependency validation
 * @param requireSnapshotValidation the require snapshot validation
 * @param requireParticipantLiveness the require participant liveness
 * @param requireQuorum the require quorum
 * @param requireAttestation the require attestation
 * @param autoRenew the auto renew
 */


public record KnowledgeAnswerResolutionRevalidationPolicy(
        Duration revalidationWindow,
        Duration renewalWindow,
        boolean revalidateBeforeUse,
        boolean allowGracePeriodUse,
        boolean requireDependencyValidation,
        boolean requireSnapshotValidation,
        boolean requireParticipantLiveness,
        boolean requireQuorum,
        boolean requireAttestation,
        boolean autoRenew
) {

    public KnowledgeAnswerResolutionRevalidationPolicy {
        Objects.requireNonNull(
                revalidationWindow,
                "revalidationWindow");

        Objects.requireNonNull(
                renewalWindow,
                "renewalWindow");

        if (revalidationWindow.isNegative()) {
            throw new IllegalArgumentException(
                    "revalidationWindow must not be negative");
        }

        if (renewalWindow.isNegative()) {
            throw new IllegalArgumentException(
                    "renewalWindow must not be negative");
        }
    }

    public static KnowledgeAnswerResolutionRevalidationPolicy defaults() {

        return new KnowledgeAnswerResolutionRevalidationPolicy(
                Duration.ofMinutes(2),
                Duration.ofMinutes(5),
                true,
                false,
                true,
                true,
                true,
                true,
                true,
                true
        );
    }
}
