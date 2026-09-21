package tech.kayys.andalus.knowledge.exchange.auth;

/**
 * Defines the contract for knowledge evidence exchange authorizer operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeAuthorizer {
    KnowledgeEvidenceExchangeAuthorizationDecision authorize(
            KnowledgeEvidenceExchangeAuthorizationContext context
    );
}
