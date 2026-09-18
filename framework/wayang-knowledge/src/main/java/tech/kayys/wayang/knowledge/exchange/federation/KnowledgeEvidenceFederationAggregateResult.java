package tech.kayys.wayang.knowledge.exchange.federation;

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


import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge evidence federation aggregate result.
 *
 * <p>Its components capture `query id`, `evidence`, `queried runtime ids`, `failed runtime ids`, `complete`, and other values.</p>
 *
 * @param queryId the query id
 * @param evidence the evidence
 * @param queriedRuntimeIds the queried runtime ids
 * @param failedRuntimeIds the failed runtime ids
 * @param complete the complete
 * @param diagnostics the diagnostics
 */


public record KnowledgeEvidenceFederationAggregateResult(

        String queryId,

        List<KnowledgeEvidenceReference> evidence,

        List<String> queriedRuntimeIds,

        List<String> failedRuntimeIds,

        boolean complete,

        Map<String, String> diagnostics

) {

    public KnowledgeEvidenceFederationAggregateResult {

        evidence = evidence == null
                ? List.of()
                : List.copyOf(evidence);

        queriedRuntimeIds = queriedRuntimeIds == null
                ? List.of()
                : List.copyOf(queriedRuntimeIds);

        failedRuntimeIds = failedRuntimeIds == null
                ? List.of()
                : List.copyOf(failedRuntimeIds);

        diagnostics = diagnostics == null
                ? Map.of()
                : Map.copyOf(diagnostics);
    }
}
