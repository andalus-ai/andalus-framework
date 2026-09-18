package tech.kayys.wayang.harness.environment.v3;

/**
 * Lifecycle states of a Wayang execution environment.
 */
public enum EnvironmentState {
    CREATING,
    READY,
    BUSY,
    DRAINING,
    STOPPING,
    STOPPED,
    FAILED
}
