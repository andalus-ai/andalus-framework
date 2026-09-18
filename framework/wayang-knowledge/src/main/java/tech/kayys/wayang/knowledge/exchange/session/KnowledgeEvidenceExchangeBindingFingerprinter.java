package tech.kayys.wayang.knowledge.exchange.session;

/**
 * Defines the contract for knowledge evidence exchange binding fingerprinter operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeBindingFingerprinter {
    String fingerprint(KnowledgeEvidenceExchangeRequestBinding binding);
}
