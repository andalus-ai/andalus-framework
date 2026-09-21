package tech.kayys.andalus.knowledge.exchange.routing;

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
 * Represents a knowledge evidence runtime retrieval request.
 *
 * <p>Its components capture `request id`, `runtime id`, `query`, `intent`, `limit`, and other values.</p>
 *
 * @param requestId the request id
 * @param runtimeId the runtime id
 * @param query the query
 * @param intent the intent
 * @param limit the limit
 * @param minScore the min score
 * @param metadata the metadata
 */


public record KnowledgeEvidenceRuntimeRetrievalRequest(

        String requestId,

        String runtimeId,

        KnowledgeEvidenceSemanticQuery query,

        KnowledgeEvidenceQueryIntent intent,

        int limit,

        double minScore,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceRuntimeRetrievalRequest {

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
