package tech.kayys.andalus.knowledge.exchange.recovery;

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
