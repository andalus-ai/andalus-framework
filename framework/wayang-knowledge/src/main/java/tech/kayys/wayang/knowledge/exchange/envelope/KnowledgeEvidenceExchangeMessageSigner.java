package tech.kayys.wayang.knowledge.exchange.envelope;

import java.time.Instant;

/**
 * Defines the contract for knowledge evidence exchange message signer operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeMessageSigner {

    KnowledgeEvidenceExchangeSignedEnvelope sign(
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
            Instant issuedAt,
            Instant expiresAt,
            Instant now
    );
}
