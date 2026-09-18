package tech.kayys.wayang.knowledge.exchange.envelope;

/**
 * Defines the knowledge evidence exchange message authentication status values used by the Wayang framework.
 */


public enum KnowledgeEvidenceExchangeMessageAuthenticationStatus {
    AUTHENTICATED,
    INVALID_SIGNATURE,
    INVALID_MAC,
    UNKNOWN_KEY,
    REVOKED_KEY,
    EXPIRED_KEY,
    UNSUPPORTED_ALGORITHM,
    INVALID_MESSAGE,
    INVALID_BINDING
}
