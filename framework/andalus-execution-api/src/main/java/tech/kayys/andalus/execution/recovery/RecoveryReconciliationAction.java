package tech.kayys.andalus.execution.recovery;

/** Outcome of reconciling an interrupted durable recovery lifecycle. */
public enum RecoveryReconciliationAction {
    NO_OP,
    ACTIVE_RECOVERY,
    PROMOTED_RUNNING,
    MARKED_SUSPENDED,
    LEASE_UNAVAILABLE
}
