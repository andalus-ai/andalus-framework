package tech.kayys.wayang.knowledge.exchange.session;

/**
 * Defines the knowledge evidence exchange replay status values used by the Wayang framework.
 */


public enum KnowledgeEvidenceExchangeReplayStatus {
    NEW,
    ACCEPTED,
    REPLAYED,
    EXPIRED,
    INVALID_BINDING,
    UNKNOWN_SESSION
}
