package tech.kayys.wayang.knowledge.snapshot.lifecycle;

/**
 * Defines the knowledge snapshot lifecycle state values used by the Wayang framework.
 */


public enum KnowledgeSnapshotLifecycleState {
    ACTIVE,
    RETAINED,
    ARCHIVED,
    DELETE_PENDING,
    DELETED,
    BLOCKED
}
