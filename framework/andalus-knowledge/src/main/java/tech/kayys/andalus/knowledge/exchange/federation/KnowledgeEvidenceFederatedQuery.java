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


import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence federated query.
 *
 * <p>Its components capture `query id`, `tenant id`, `workspace id`, `project id`, `agent id`, and other values.</p>
 *
 * @param queryId the query id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param agentId the agent id
 * @param text the text
 * @param limit the limit
 * @param minScore the min score
 * @param effectiveAt the effective at
 * @param requireVerification the require verification
 * @param requireAuthorization the require authorization
 * @param allowRemote the allow remote
 * @param filters the filters
 * @param metadata the metadata
 */


public record KnowledgeEvidenceFederatedQuery(

        String queryId,

        String tenantId,

        String workspaceId,

        String projectId,

        String agentId,

        String text,

        int limit,

        double minScore,

        Instant effectiveAt,

        boolean requireVerification,

        boolean requireAuthorization,

        boolean allowRemote,

        Map<String, String> filters,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceFederatedQuery {

        if (queryId == null || queryId.isBlank()) {
            throw new IllegalArgumentException(
                    "queryId is required"
            );
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "query text is required"
            );
        }

        if (limit < 1) {
            throw new IllegalArgumentException(
                    "limit must be positive"
            );
        }

        filters = filters == null
                ? Map.of()
                : Map.copyOf(filters);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
