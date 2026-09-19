package tech.kayys.wayang.execution.environment;

/**
 * Lifecycle states of an execution environment.
 */
public enum EnvironmentState {
    PROVISIONING,
    READY,
    ACTIVE,
    PAUSED,
    DRAINING,
    TERMINATED,
    FAILED
}
