package tech.kayys.wayang.knowledge.seal;

/**
 * Defines the knowledge snapshot seal status values used by the Wayang framework.
 */


public enum KnowledgeSnapshotSealStatus {
    SEALED,
    VERIFIED,
    INVALID,
    EXPIRED,
    REVOKED,
    UNTRUSTED,
    INCOMPLETE
}
