package tech.kayys.wayang.knowledge.exchange.session;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge evidence exchange handshake.
 *
 * <p>Its components capture `handshake id`, `local runtime id`, `remote runtime id`, `principal id`, `tenant id`, and other values.</p>
 *
 * @param handshakeId the handshake id
 * @param localRuntimeId the local runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param principalId the principal id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param nonce the nonce
 * @param createdAt the created at
 * @param expiresAt the expires at
 * @param capabilities the capabilities
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeHandshake(
        String handshakeId,
        String localRuntimeId,
        String remoteRuntimeId,
        String principalId,
        String tenantId,
        String workspaceId,
        String projectId,
        String nonce,
        Instant createdAt,
        Instant expiresAt,
        Map<String, String> capabilities,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceExchangeHandshake {
        Objects.requireNonNull(handshakeId, "handshakeId");
        Objects.requireNonNull(localRuntimeId, "localRuntimeId");
        Objects.requireNonNull(remoteRuntimeId, "remoteRuntimeId");
        Objects.requireNonNull(nonce, "nonce");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(expiresAt, "expiresAt");

        capabilities = capabilities == null ? Map.of() : Map.copyOf(capabilities);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }
}
