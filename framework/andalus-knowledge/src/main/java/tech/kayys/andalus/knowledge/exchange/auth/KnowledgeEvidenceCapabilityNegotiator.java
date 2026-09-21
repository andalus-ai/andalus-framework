package tech.kayys.andalus.knowledge.exchange.auth;

import tech.kayys.andalus.knowledge.exchange.KnowledgeEvidenceExchangeCapabilities;

/**
 * Defines the contract for knowledge evidence capability negotiator operations in the Wayang framework.
 */


public interface KnowledgeEvidenceCapabilityNegotiator {
    KnowledgeEvidenceCapabilityNegotiationResult negotiate(
            KnowledgeEvidenceExchangeCapabilities remote,
            KnowledgeEvidenceCapabilityRequirement required
    );
}
