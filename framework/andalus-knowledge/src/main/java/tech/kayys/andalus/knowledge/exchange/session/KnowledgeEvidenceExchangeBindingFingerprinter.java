package tech.kayys.andalus.knowledge.exchange.session;

/**
 * Defines the contract for knowledge evidence exchange binding fingerprinter operations in the Andalus framework.
 */


public interface KnowledgeEvidenceExchangeBindingFingerprinter {
    String fingerprint(KnowledgeEvidenceExchangeRequestBinding binding);
}
