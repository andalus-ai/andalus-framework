package tech.kayys.andalus.knowledge.exchange.fusion;

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


import java.util.Map;

/**
 * Represents a knowledge evidence fusion candidate.
 *
 * <p>Its components capture `evidence`, `runtime id`, `retrieval score`, `semantic score`, `authority score`, and other values.</p>
 *
 * @param evidence the evidence
 * @param runtimeId the runtime id
 * @param retrievalScore the retrieval score
 * @param semanticScore the semantic score
 * @param authorityScore the authority score
 * @param trustScore the trust score
 * @param freshnessScore the freshness score
 * @param finalScore the final score
 * @param metadata the metadata
 */


public record KnowledgeEvidenceFusionCandidate(

        KnowledgeEvidenceReference evidence,

        String runtimeId,

        double retrievalScore,

        double semanticScore,

        double authorityScore,

        double trustScore,

        double freshnessScore,

        double finalScore,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceFusionCandidate {

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
