package tech.kayys.wayang.knowledge.exchange.auth;

import java.util.Map;
import java.util.Set;

/**
 * Represents a knowledge evidence exchange principal.
 *
 * <p>Its components capture `principal id`, `runtime id`, `agent id`, `actor id`, `tenant id`, and other values.</p>
 *
 * @param principalId the principal id
 * @param runtimeId the runtime id
 * @param agentId the agent id
 * @param actorId the actor id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param userId the user id
 * @param roles the roles
 * @param attributes the attributes
 */


public record KnowledgeEvidenceExchangePrincipal(
        String principalId,
        String runtimeId,
        String agentId,
        String actorId,
        String tenantId,
        String workspaceId,
        String projectId,
        String userId,
        Set<String> roles,
        Map<String, String> attributes
) {
    public KnowledgeEvidenceExchangePrincipal {
        roles = roles == null ? Set.of() : Set.copyOf(roles);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static KnowledgeEvidenceExchangePrincipal of(String principalId, String runtimeId, String tenantId) {
        return new KnowledgeEvidenceExchangePrincipal(
                principalId,
                runtimeId,
                null,
                null,
                tenantId,
                null,
                null,
                null,
                Set.of(),
                Map.of()
        );
    }
}
