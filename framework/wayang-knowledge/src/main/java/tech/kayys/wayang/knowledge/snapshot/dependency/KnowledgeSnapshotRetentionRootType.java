package tech.kayys.wayang.knowledge.snapshot.dependency;

/**
 * Defines the knowledge snapshot retention root type values used by the Wayang framework.
 */


public enum KnowledgeSnapshotRetentionRootType {
    ACTIVE_EXECUTION,
    DECISION_TRACE,
    AUDIT_EVENT,
    REPLAY,
    DERIVATION,
    LEGAL_HOLD,
    MANUAL_HOLD,
    EXTERNAL_REFERENCE
}
