package tech.kayys.andalus.harness.kernel;

/**
 * Lifecycle states of the Andalus Harness Kernel.
 */
public enum HarnessState {
    CREATED,
    INITIALIZING,
    INITIALIZED,
    STARTING,
    READY,
    RUNNING,
    DRAINING,
    STOPPING,
    STOPPED,
    FAILED
}
