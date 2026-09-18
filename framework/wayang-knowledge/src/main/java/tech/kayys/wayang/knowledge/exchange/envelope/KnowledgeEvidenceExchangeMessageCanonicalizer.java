package tech.kayys.wayang.knowledge.exchange.envelope;

/**
 * Defines the contract for knowledge evidence exchange message canonicalizer operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeMessageCanonicalizer {
    byte[] canonicalize(KnowledgeEvidenceExchangeSignedEnvelope envelope);
}
