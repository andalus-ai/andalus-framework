package tech.kayys.wayang.knowledge.exchange.binding;

import tech.kayys.wayang.knowledge.exchange.KnowledgeEvidenceExchangeOperation;
import tech.kayys.wayang.knowledge.snapshot.artifact.KnowledgeEvidenceArtifactId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge evidence exchange response binding.
 *
 * <p>Its components capture `request id`, `session id`, `request nonce`, `response id`, `runtime id`, and other values.</p>
 *
 * @param requestId the request id
 * @param sessionId the session id
 * @param requestNonce the request nonce
 * @param responseId the response id
 * @param runtimeId the runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param operation the operation
 * @param artifactId the artifact id
 * @param resourceId the resource id
 * @param success the success
 * @param contentFingerprint the content fingerprint
 * @param manifestFingerprint the manifest fingerprint
 * @param merkleProofFingerprint the merkle proof fingerprint
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param responseFingerprint the response fingerprint
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeResponseBinding(
        String requestId,
        String sessionId,
        String requestNonce,
        String responseId,
        String runtimeId,
        String remoteRuntimeId,
        String tenantId,
        String workspaceId,
        String projectId,
        KnowledgeEvidenceExchangeOperation operation,
        KnowledgeEvidenceArtifactId artifactId,
        String resourceId,
        boolean success,
        String contentFingerprint,
        String manifestFingerprint,
        String merkleProofFingerprint,
        Instant issuedAt,
        Instant expiresAt,
        String responseFingerprint,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceExchangeResponseBinding {
        Objects.requireNonNull(requestId, "requestId");
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(requestNonce, "requestNonce");
        Objects.requireNonNull(responseId, "responseId");
        Objects.requireNonNull(runtimeId, "runtimeId");
        Objects.requireNonNull(operation, "operation");
        Objects.requireNonNull(issuedAt, "issuedAt");
        Objects.requireNonNull(expiresAt, "expiresAt");
        Objects.requireNonNull(responseFingerprint, "responseFingerprint");

        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }
}
