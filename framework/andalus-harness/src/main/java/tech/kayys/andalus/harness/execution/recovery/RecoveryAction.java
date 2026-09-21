package tech.kayys.andalus.harness.execution.recovery;

/**
 * Defines the recovery action values used by the Andalus framework.
 */


public enum RecoveryAction {
    RESUME,
    RETRY,
    ROLLBACK,
    RESTART_PHASE,
    /** Trigger saga-style compensation for already-committed effects. */
    COMPENSATE,
    WAIT,
    FAIL
}
