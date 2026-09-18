package tech.kayys.wayang.knowledge.exchange.session;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge evidence exchange session.
 *
 * <p>Its components capture `session id`, `local runtime id`, `remote runtime id`, `principal id`, `tenant id`, and other values.</p>
 *
 * @param sessionId the session id
 * @param localRuntimeId the local runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param principalId the principal id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param createdAt the created at
 * @param expiresAt the expires at
 * @param status the status
 * @param localNonce the local nonce
 * @param remoteNonce the remote nonce
 * @param sessionFingerprint the session fingerprint
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeSession(
        String sessionId,
        String localRuntimeId,
        String remoteRuntimeId,
        String principalId,
        String tenantId,
        String workspaceId,
        String projectId,
        Instant createdAt,
        Instant expiresAt,
        KnowledgeEvidenceExchangeSessionStatus status,
        String localNonce,
        String remoteNonce,
        String sessionFingerprint,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceExchangeSession {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(localRuntimeId, "localRuntimeId");
        Objects.requireNonNull(remoteRuntimeId, "remoteRuntimeId");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(expiresAt, "expiresAt");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(localNonce, "localNonce");
        Objects.requireNonNull(sessionFingerprint, "sessionFingerprint");

        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean isExpired(Instant now) {
        Objects.requireNonNull(now, "now");
        return now.isAfter(expiresAt);
    }

    public boolean isActive(Instant now) {
        return status == KnowledgeEvidenceExchangeSessionStatus.ESTABLISHED && !isExpired(now);
    }
}
