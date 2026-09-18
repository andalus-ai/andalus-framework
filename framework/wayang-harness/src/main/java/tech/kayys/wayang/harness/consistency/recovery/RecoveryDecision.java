package tech.kayys.wayang.harness.consistency.recovery;

/**
 * Tactical decision made by a recovery policy when an execution fails.
 */
public enum RecoveryDecision {
    RETRY,
    RESUME,
    COMPENSATE,
    FAIL
}
