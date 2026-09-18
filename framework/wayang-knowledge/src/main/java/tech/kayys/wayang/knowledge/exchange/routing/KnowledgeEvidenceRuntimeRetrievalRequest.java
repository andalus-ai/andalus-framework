package tech.kayys.wayang.knowledge.exchange.routing;

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
