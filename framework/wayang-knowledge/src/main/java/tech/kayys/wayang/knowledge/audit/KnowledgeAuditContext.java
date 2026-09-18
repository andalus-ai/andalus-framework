package tech.kayys.wayang.knowledge.audit;

import java.util.Map;

/**
 * Represents a knowledge audit context.
 *
 * <p>Its components capture `actor id`, `tenant id`, `workspace id`, `project id`, `metadata`.</p>
 *
 * @param actorId the actor id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param metadata the metadata
 */


public record KnowledgeAuditContext(
        String actorId,
        String tenantId,
        String workspaceId,
        String projectId,
        Map<String, Object> metadata
) {

    public KnowledgeAuditContext {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
