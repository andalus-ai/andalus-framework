package tech.kayys.wayang.knowledge.exchange.binding;

/**
 * Defines the contract for knowledge evidence exchange response fingerprinter operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeResponseFingerprinter {
    String fingerprint(KnowledgeEvidenceExchangeResponseBinding binding);
}
