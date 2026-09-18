package tech.kayys.wayang.knowledge.exchange.session;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge evidence exchange replay record.
 *
 * <p>Its components capture `request id`, `session id`, `nonce`, `binding fingerprint`, `principal id`, and other values.</p>
 *
 * @param requestId the request id
 * @param sessionId the session id
 * @param nonce the nonce
 * @param bindingFingerprint the binding fingerprint
 * @param principalId the principal id
 * @param tenantId the tenant id
 * @param runtimeId the runtime id
 * @param firstSeenAt the first seen at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeReplayRecord(
        String requestId,
        String sessionId,
        String nonce,
        String bindingFingerprint,
        String principalId,
        String tenantId,
        String runtimeId,
        Instant firstSeenAt,
        Instant expiresAt,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceExchangeReplayRecord {
        Objects.requireNonNull(requestId, "requestId");
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(nonce, "nonce");
        Objects.requireNonNull(bindingFingerprint, "bindingFingerprint");
        Objects.requireNonNull(firstSeenAt, "firstSeenAt");
        Objects.requireNonNull(expiresAt, "expiresAt");

        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean expired(Instant now) {
        return now.isAfter(expiresAt);
    }
}
