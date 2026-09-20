package tech.kayys.wayang.execution.control;

/**
 * Standard lifecycle control actions in Wayang Control Plane.
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
