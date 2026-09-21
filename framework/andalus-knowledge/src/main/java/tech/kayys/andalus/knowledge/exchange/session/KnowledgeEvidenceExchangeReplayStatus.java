package tech.kayys.andalus.knowledge.exchange.session;

/**
 * Defines the knowledge evidence exchange replay status values used by the Andalus framework.
 */


public enum KnowledgeEvidenceExchangeReplayStatus {
    NEW,
    ACCEPTED,
    REPLAYED,
    EXPIRED,
    INVALID_BINDING,
    UNKNOWN_SESSION
}
