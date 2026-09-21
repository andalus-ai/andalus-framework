package tech.kayys.andalus.harness.environment.v3;

/**
 * Lifecycle states of a Andalus execution environment.
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
