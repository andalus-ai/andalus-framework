package tech.kayys.wayang.knowledge.exchange.envelope;

/**
 * Defines the knowledge evidence exchange message authentication algorithm values used by the Wayang framework.
 */


public enum KnowledgeEvidenceExchangeMessageAuthenticationAlgorithm {
    HMAC_SHA256,
    ED25519,
    ECDSA_SHA256
}
