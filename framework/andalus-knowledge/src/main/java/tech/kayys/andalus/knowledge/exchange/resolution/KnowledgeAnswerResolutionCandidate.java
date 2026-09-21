package tech.kayys.andalus.knowledge.exchange.resolution;

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


import java.util.Map;

/**
 * Represents a knowledge answer resolution candidate.
 *
 * <p>Its components capture `artifact id`, `runtime id`, `authority score`, `trust score`, `freshness score`, and other values.</p>
 *
 * @param artifactId the artifact id
 * @param runtimeId the runtime id
 * @param authorityScore the authority score
 * @param trustScore the trust score
 * @param freshnessScore the freshness score
 * @param provenanceScore the provenance score
 * @param scopeScore the scope score
 * @param supportScore the support score
 * @param conflictPenalty the conflict penalty
 * @param finalScore the final score
 * @param verified the verified
 * @param eligible the eligible
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionCandidate(

        String artifactId,

        String runtimeId,

        double authorityScore,

        double trustScore,

        double freshnessScore,

        double provenanceScore,

        double scopeScore,

        double supportScore,

        double conflictPenalty,

        double finalScore,

        boolean verified,

        boolean eligible,

        Map<String, String> metadata
) {

    public KnowledgeAnswerResolutionCandidate {
        metadata =
                metadata == null
                        ? Map.of()
                        : Map.copyOf(metadata);
    }
}
