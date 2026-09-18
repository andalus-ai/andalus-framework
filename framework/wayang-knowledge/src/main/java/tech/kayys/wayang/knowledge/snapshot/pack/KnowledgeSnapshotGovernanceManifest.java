package tech.kayys.wayang.knowledge.snapshot.pack;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge snapshot governance manifest.
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


public record KnowledgeSnapshotGovernanceManifest(
        String tenantId,
        String workspaceId,
        String projectId,
        String userId,
        Instant effectiveAt,
        String scopeFingerprint,
        String governancePolicyFingerprint,
        Map<String, String> attributes
) {
    public KnowledgeSnapshotGovernanceManifest {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
