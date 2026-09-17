package tech.kayys.wayang.harness.context;

/**
 * State of continuity for a Harness session.
 */
public enum SessionState {
    NEW,
    ACTIVE,
    SUSPENDED,
    COMPLETED,
    CLOSED
}
