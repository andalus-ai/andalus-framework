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


import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge evidence retrieval plan.
 *
 * <p>Its components capture `strategy`, `targets`, `maximum runtime calls`, `evidence budget`, `stop when satisfied`, and other values.</p>
 *
 * @param strategy the strategy
 * @param targets the targets
 * @param maximumRuntimeCalls the maximum runtime calls
 * @param evidenceBudget the evidence budget
 * @param stopWhenSatisfied the stop when satisfied
 * @param metadata the metadata
 */


public record KnowledgeEvidenceRetrievalPlan(

        KnowledgeEvidenceFederatedRetrievalStrategy strategy,

        List<KnowledgeEvidenceRetrievalTarget> targets,

        int maximumRuntimeCalls,

        int evidenceBudget,

        boolean stopWhenSatisfied,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceRetrievalPlan {

        targets = targets == null
                ? List.of()
                : List.copyOf(targets);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
