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


import java.time.Instant;
import java.util.List;

/**
 * Represents a knowledge answer resolution revalidation result.
 *
 * <p>Its components capture `status`, `consensus id`, `key fingerprint`, `previous freshness`, `current freshness`, and other values.</p>
 *
 * @param status the status
 * @param consensusId the consensus id
 * @param keyFingerprint the key fingerprint
 * @param previousFreshness the previous freshness
 * @param currentFreshness the current freshness
 * @param dependenciesValid the dependencies valid
 * @param snapshotValid the snapshot valid
 * @param participantsLive the participants live
 * @param quorumValid the quorum valid
 * @param attestationValid the attestation valid
 * @param usable the usable
 * @param evaluatedAt the evaluated at
 * @param diagnostics the diagnostics
 */


public record KnowledgeAnswerResolutionRevalidationResult(
        KnowledgeAnswerResolutionRevalidationStatus status,
        String consensusId,
        String keyFingerprint,
        KnowledgeAnswerResolutionFreshnessStatus previousFreshness,
        KnowledgeAnswerResolutionFreshnessStatus currentFreshness,
        boolean dependenciesValid,
        boolean snapshotValid,
        boolean participantsLive,
        boolean quorumValid,
        boolean attestationValid,
        boolean usable,
        Instant evaluatedAt,
        List<String> diagnostics
) {

    public KnowledgeAnswerResolutionRevalidationResult {
        diagnostics = diagnostics == null
                ? List.of()
                : List.copyOf(diagnostics);
    }
}
