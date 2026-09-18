package tech.kayys.wayang.harness.controlplane;

/**
 * Standard control-plane command actions.
 */
public enum CommandType {
    START_EXECUTION,
    CANCEL_EXECUTION,
    PAUSE_EXECUTION,
    RESUME_EXECUTION,
    ASSIGN_NODE,
    APPROVE_OPERATION,
    DRAIN_WORKER
}
