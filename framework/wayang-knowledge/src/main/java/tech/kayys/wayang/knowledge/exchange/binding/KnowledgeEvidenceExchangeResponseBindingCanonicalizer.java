package tech.kayys.wayang.knowledge.exchange.binding;

/**
 * Defines the contract for knowledge evidence exchange response binding canonicalizer operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeResponseBindingCanonicalizer {
    String canonicalize(KnowledgeEvidenceExchangeResponseBinding binding);
}
