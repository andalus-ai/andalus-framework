package tech.kayys.wayang.knowledge.governance;

/**
 * Defines the knowledge operation values used by the Wayang framework.
 */


public enum KnowledgeOperation {
    READ,
    SEARCH,
    RETRIEVE,
    INJECT_CONTEXT,

    CREATE,
    UPDATE,
    CORRECT,
    SUPERSEDE,
    DEPRECATE,
    DELETE,

    CHANGE_MEMBERSHIP,
    CHANGE_QUORUM,
    CREATE_EPOCH,
    REVOKE_PARTICIPANT,
    FINALIZE_MEMBERSHIP_TRANSITION
}
