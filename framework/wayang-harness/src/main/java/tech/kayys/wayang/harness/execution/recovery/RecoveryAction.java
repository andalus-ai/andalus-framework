package tech.kayys.wayang.harness.execution.recovery;

/**
 * Defines the recovery action values used by the Wayang framework.
 */


public enum RecoveryAction {
    RESUME,
    RETRY,
    ROLLBACK,
    RESTART_PHASE,
    WAIT,
    FAIL
}
