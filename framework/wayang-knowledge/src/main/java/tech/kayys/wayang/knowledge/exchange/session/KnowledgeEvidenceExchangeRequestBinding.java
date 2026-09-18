package tech.kayys.wayang.knowledge.exchange.session;

import tech.kayys.wayang.knowledge.exchange.KnowledgeEvidenceExchangeOperation;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge evidence exchange request binding.
 *
 * <p>Its components capture `request id`, `session id`, `nonce`, `runtime id`, `remote runtime id`, and other values.</p>
 *
 * @param requestId the request id
 * @param sessionId the session id
 * @param nonce the nonce
 * @param runtimeId the runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param operation the operation
 * @param artifactId the artifact id
 * @param resourceId the resource id
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param correlationId the correlation id
 * @param bindingFingerprint the binding fingerprint
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeRequestBinding(
        String requestId,
        String sessionId,
        String nonce,
        String runtimeId,
        String remoteRuntimeId,
        String tenantId,
        String workspaceId,
        String projectId,
        KnowledgeEvidenceExchangeOperation operation,
        String artifactId,
        String resourceId,
        Instant issuedAt,
        Instant expiresAt,
        String correlationId,
        String bindingFingerprint,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceExchangeRequestBinding {
        Objects.requireNonNull(requestId, "requestId");
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(nonce, "nonce");
        Objects.requireNonNull(runtimeId, "runtimeId");
        Objects.requireNonNull(operation, "operation");
        Objects.requireNonNull(issuedAt, "issuedAt");
        Objects.requireNonNull(expiresAt, "expiresAt");
        Objects.requireNonNull(bindingFingerprint, "bindingFingerprint");

        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }
}
