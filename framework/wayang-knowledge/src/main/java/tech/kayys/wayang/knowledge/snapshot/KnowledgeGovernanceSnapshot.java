package tech.kayys.wayang.knowledge.snapshot;

import java.util.Map;

/**
 * Represents a knowledge governance snapshot.
 *
 * <p>Its components capture `tenant id`, `workspace id`, `project id`, `user id`, `effective at`, and other values.</p>
 *
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param userId the user id
 * @param effectiveAt the effective at
 * @param scopeFingerprint the scope fingerprint
 * @param governancePolicyFingerprint the governance policy fingerprint
 * @param attributes the attributes
 */


public record KnowledgeGovernanceSnapshot(
        String tenantId,
        String workspaceId,
        String projectId,
        String userId,
        String effectiveAt,
        String scopeFingerprint,
        String governancePolicyFingerprint,
        Map<String, Object> attributes
) {

    public KnowledgeGovernanceSnapshot {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
