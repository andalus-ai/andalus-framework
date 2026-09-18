package tech.kayys.wayang.knowledge.exchange.binding;

import java.util.Map;

/**
 * Represents a knowledge evidence exchange response verification result.
 *
 * <p>Its components capture `status`, `request id`, `response id`, `expected fingerprint`, `actual fingerprint`, and other values.</p>
 *
 * @param status the status
 * @param requestId the request id
 * @param responseId the response id
 * @param expectedFingerprint the expected fingerprint
 * @param actualFingerprint the actual fingerprint
 * @param reason the reason
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeResponseVerificationResult(
        KnowledgeEvidenceExchangeResponseVerificationStatus status,
        String requestId,
        String responseId,
        String expectedFingerprint,
        String actualFingerprint,
        String reason,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceExchangeResponseVerificationResult {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean valid() {
        return status == KnowledgeEvidenceExchangeResponseVerificationStatus.VALID;
    }
}
