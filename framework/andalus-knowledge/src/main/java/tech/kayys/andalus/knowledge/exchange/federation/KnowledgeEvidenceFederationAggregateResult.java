package tech.kayys.andalus.knowledge.exchange.federation;

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
