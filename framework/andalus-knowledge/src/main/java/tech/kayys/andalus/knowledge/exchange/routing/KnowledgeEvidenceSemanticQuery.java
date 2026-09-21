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


import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge evidence semantic query.
 *
 * <p>Its components capture `query id`, `text`, `tenant id`, `workspace id`, `project id`, and other values.</p>
 *
 * @param queryId the query id
 * @param text the text
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param agentId the agent id
 * @param limit the limit
 * @param minScore the min score
 * @param effectiveAt the effective at
 * @param requiredKnowledgeTypes the required knowledge types
 * @param requiredTags the required tags
 * @param strategy the strategy
 * @param requireVerification the require verification
 * @param allowRemote the allow remote
 * @param metadata the metadata
 */


public record KnowledgeEvidenceSemanticQuery(

        String queryId,

        String text,

        String tenantId,

        String workspaceId,

        String projectId,

        String agentId,

        int limit,

        double minScore,

        Instant effectiveAt,

        List<String> requiredKnowledgeTypes,

        List<String> requiredTags,

        KnowledgeEvidenceFederatedRetrievalStrategy strategy,

        boolean requireVerification,

        boolean allowRemote,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceSemanticQuery {

        if (queryId == null || queryId.isBlank()) {
            throw new IllegalArgumentException("queryId is required");
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text is required");
        }

        if (limit < 1) {
            throw new IllegalArgumentException(
                    "limit must be positive"
            );
        }

        requiredKnowledgeTypes =
                requiredKnowledgeTypes == null
                        ? List.of()
                        : List.copyOf(requiredKnowledgeTypes);

        requiredTags =
                requiredTags == null
                        ? List.of()
                        : List.copyOf(requiredTags);

        metadata =
                metadata == null
                        ? Map.of()
                        : Map.copyOf(metadata);
    }
}
