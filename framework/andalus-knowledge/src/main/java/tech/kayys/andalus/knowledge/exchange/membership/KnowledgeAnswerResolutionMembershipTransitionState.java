package tech.kayys.andalus.knowledge.exchange.membership;

/**
 * Defines the knowledge answer resolution membership transition state values used by the Wayang framework.
 */


public enum KnowledgeAnswerResolutionMembershipTransitionState {
    PROPOSED,
    JOINT,
    FINALIZING,
    COMPLETED,
    ABORTED,
    REVOKED
}
