package tech.kayys.wayang.knowledge.exchange.membership;

/**
 * Defines the knowledge answer resolution membership change type values used by the Wayang framework.
 */


public enum KnowledgeAnswerResolutionMembershipChangeType {
    ADD_RUNTIME,
    REMOVE_RUNTIME,
    REPLACE_RUNTIME,
    CHANGE_QUORUM,
    CHANGE_POLICY,
    ROTATE_MEMBERSHIP,
    REVOKE_RUNTIME
}
