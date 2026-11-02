package tech.kayys.andalus.execution.recovery;

/** Classification used to decide whether a failed recovery may be attempted automatically. */
public enum RecoveryFailureCategory {
    TRANSIENT,
    PERMANENT,
    OPERATOR_ACTION_REQUIRED,
    CANCELLED,
    DEADLINE_EXCEEDED
}
