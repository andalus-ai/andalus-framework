package tech.kayys.wayang.harness.kernel;

/**
 * Lifecycle states of the Wayang Harness Kernel.
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
