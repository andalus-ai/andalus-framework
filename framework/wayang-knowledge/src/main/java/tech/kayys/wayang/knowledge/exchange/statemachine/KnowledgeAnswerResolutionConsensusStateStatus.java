package tech.kayys.wayang.knowledge.exchange.statemachine;

/**
 * Defines the knowledge answer resolution consensus state status values used by the Wayang framework.
 */


public enum KnowledgeAnswerResolutionConsensusStateStatus {
    PROPOSED,
    ACTIVE,
    SUPERSEDED,
    REVOKED,
    EXPIRED,
    REJECTED
}
