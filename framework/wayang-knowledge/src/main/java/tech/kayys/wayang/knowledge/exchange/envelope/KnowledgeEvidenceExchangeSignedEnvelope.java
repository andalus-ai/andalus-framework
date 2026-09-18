package tech.kayys.wayang.knowledge.exchange.envelope;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge evidence exchange signed envelope.
 *
 * <p>Its components capture `envelope id`, `request id`, `session id`, `nonce`, `runtime id`, and other values.</p>
 *
 * @param envelopeId the envelope id
 * @param requestId the request id
 * @param sessionId the session id
 * @param nonce the nonce
 * @param runtimeId the runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param messageType the message type
 * @param messageFingerprint the message fingerprint
 * @param authenticationAlgorithm the authentication algorithm
 * @param keyId the key id
 * @param keyVersion the key version
 * @param authentication the authentication
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeSignedEnvelope(
        String envelopeId,
        String requestId,
        String sessionId,
        String nonce,
        String runtimeId,
        String remoteRuntimeId,
        String tenantId,
        String workspaceId,
        String projectId,
        String messageType,
        String messageFingerprint,
        KnowledgeEvidenceExchangeMessageAuthenticationAlgorithm authenticationAlgorithm,
        String keyId,
        String keyVersion,
        byte[] authentication,
        Instant issuedAt,
        Instant expiresAt,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceExchangeSignedEnvelope {
        Objects.requireNonNull(envelopeId, "envelopeId");
        Objects.requireNonNull(requestId, "requestId");
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(nonce, "nonce");
        Objects.requireNonNull(runtimeId, "runtimeId");
        Objects.requireNonNull(messageType, "messageType");
        Objects.requireNonNull(messageFingerprint, "messageFingerprint");
        Objects.requireNonNull(authenticationAlgorithm, "authenticationAlgorithm");
        Objects.requireNonNull(keyId, "keyId");
        Objects.requireNonNull(keyVersion, "keyVersion");
        Objects.requireNonNull(issuedAt, "issuedAt");
        Objects.requireNonNull(expiresAt, "expiresAt");

        authentication = authentication == null ? new byte[0] : authentication.clone();
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    @Override
    public byte[] authentication() {
        return authentication.clone();
    }

    public boolean expired(Instant now) {
        return now.isAfter(expiresAt);
    }
}
