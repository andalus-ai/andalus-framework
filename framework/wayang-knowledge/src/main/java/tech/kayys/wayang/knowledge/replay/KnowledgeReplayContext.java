package tech.kayys.wayang.knowledge.replay;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge replay context.
 *
 * <p>Its components capture `tenant id`, `workspace id`, `project id`, `effective at`, `attributes`.</p>
 *
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param effectiveAt the effective at
 * @param attributes the attributes
 */


public record KnowledgeReplayContext(
        String tenantId,
        String workspaceId,
        String projectId,
        Instant effectiveAt,
        Map<String, Object> attributes
) {

    public KnowledgeReplayContext {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
