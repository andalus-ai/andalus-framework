package tech.kayys.wayang.knowledge.snapshot.lifecycle;

/**
 * Defines the knowledge snapshot deletion decision values used by the Wayang framework.
 */


public enum KnowledgeSnapshotDeletionDecision {
    DELETE,
    RETAIN,
    ARCHIVE,
    BLOCKED
}
