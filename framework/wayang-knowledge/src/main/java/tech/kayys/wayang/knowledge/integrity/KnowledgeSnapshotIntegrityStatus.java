package tech.kayys.wayang.knowledge.integrity;

/**
 * Defines the knowledge snapshot integrity status values used by the Wayang framework.
 */


public enum KnowledgeSnapshotIntegrityStatus {
    ATTESTED,
    TAMPERED,
    INCOMPLETE,
    BLOCKED,
    FAILED
}
