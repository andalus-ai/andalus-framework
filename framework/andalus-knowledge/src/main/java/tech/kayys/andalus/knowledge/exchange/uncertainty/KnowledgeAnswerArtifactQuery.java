package tech.kayys.andalus.knowledge.exchange.uncertainty;

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


import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Represents a knowledge answer artifact query.
 *
 * <p>Its components capture `query id`, `text`, `tenant id`, `workspace id`, `project id`, and other values.</p>
 *
 * @param queryId the query id
 * @param text the text
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param requestingRuntimeId the requesting runtime id
 * @param agentId the agent id
 * @param limit the limit
 * @param minScore the min score
 * @param effectiveAt the effective at
 * @param requiredTags the required tags
 * @param requiredAgents the required agents
 * @param requiredStatuses the required statuses
 * @param requireVerified the require verified
 * @param requireSealed the require sealed
 * @param allowRemote the allow remote
 * @param metadata the metadata
 */


public record KnowledgeAnswerArtifactQuery(

        String queryId,

        String text,

        String tenantId,

        String workspaceId,

        String projectId,

        String requestingRuntimeId,

        String agentId,

        int limit,

        double minScore,

        Instant effectiveAt,

        Set<String> requiredTags,

        Set<String> requiredAgents,

        Set<String> requiredStatuses,

        boolean requireVerified,

        boolean requireSealed,

        boolean allowRemote,

        Map<String, String> metadata
) {

    public KnowledgeAnswerArtifactQuery {
        if (limit <= 0) {
            limit = 10;
        }

        requiredTags =
                requiredTags == null
                        ? Set.of()
                        : Set.copyOf(requiredTags);

        requiredAgents =
                requiredAgents == null
                        ? Set.of()
                        : Set.copyOf(requiredAgents);

        requiredStatuses =
                requiredStatuses == null
                        ? Set.of()
                        : Set.copyOf(requiredStatuses);

        metadata =
                metadata == null
                        ? Map.of()
                        : Map.copyOf(metadata);
    }
}
