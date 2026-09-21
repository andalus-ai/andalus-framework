package tech.kayys.andalus.knowledge.exchange.session;

/**
 * Defines the contract for knowledge evidence exchange binding canonicalizer operations in the Andalus framework.
 */


public interface KnowledgeEvidenceExchangeBindingCanonicalizer {
    String canonicalize(KnowledgeEvidenceExchangeRequestBinding binding);
}
