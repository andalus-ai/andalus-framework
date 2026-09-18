package tech.kayys.wayang.knowledge.audit;

import java.time.Instant;

/**
 * Represents a knowledge audit query.
 *
 * <p>Its components capture `tenant id`, `workspace id`, `project id`, `agent id`, `execution id`, and other values.</p>
 *
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param agentId the agent id
 * @param executionId the execution id
 * @param operation the operation
 * @param from the from
 * @param until the until
 * @param limit the limit
 */


public record KnowledgeAuditQuery(
        String tenantId,
        String workspaceId,
        String projectId,
        String agentId,
        String executionId,
        String operation,
        Instant from,
        Instant until,
        int limit
) {

    public KnowledgeAuditQuery {
        if (limit <= 0) {
            limit = 100;
        }
    }
}
