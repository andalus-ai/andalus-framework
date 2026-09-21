package tech.kayys.andalus.execution.control;

/**
 * Standard lifecycle control actions in Andalus Control Plane.
 */
public enum ControlAction {
    START,
    CONTINUE,
    PAUSE,
    RESUME,
    CHECKPOINT,
    MIGRATE,
    RETRY,
    RECOVER,
    CANCEL,
    TERMINATE,
    QUARANTINE
}
