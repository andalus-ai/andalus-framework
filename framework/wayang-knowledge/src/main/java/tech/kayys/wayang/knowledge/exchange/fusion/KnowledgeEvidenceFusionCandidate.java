package tech.kayys.wayang.knowledge.exchange.fusion;

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
