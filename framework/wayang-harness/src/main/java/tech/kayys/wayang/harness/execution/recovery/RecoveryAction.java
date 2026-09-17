package tech.kayys.wayang.harness.execution.recovery;

public enum RecoveryAction {
    RESUME,
    RETRY,
    ROLLBACK,
    RESTART_PHASE,
    WAIT,
    FAIL
}
