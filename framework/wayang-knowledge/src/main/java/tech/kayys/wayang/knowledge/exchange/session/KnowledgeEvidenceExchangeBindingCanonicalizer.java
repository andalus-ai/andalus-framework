package tech.kayys.wayang.knowledge.exchange.session;

/**
 * Defines the contract for knowledge evidence exchange binding canonicalizer operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeBindingCanonicalizer {
    String canonicalize(KnowledgeEvidenceExchangeRequestBinding binding);
}
