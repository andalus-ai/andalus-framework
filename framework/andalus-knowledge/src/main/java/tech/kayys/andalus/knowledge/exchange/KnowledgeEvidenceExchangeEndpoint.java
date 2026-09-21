package tech.kayys.andalus.knowledge.exchange;

/**
 * Defines the contract for knowledge evidence exchange endpoint operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeEndpoint {
    KnowledgeEvidenceExchangeResponse exchange(
            KnowledgeEvidenceExchangeRequest request
    );
}
