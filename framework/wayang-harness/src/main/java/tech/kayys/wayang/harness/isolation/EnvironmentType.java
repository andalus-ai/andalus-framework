package tech.kayys.wayang.harness.isolation;

/**
 * Standard classification of execution environments.
 */
public enum EnvironmentType {
    IN_PROCESS,
    LOCAL_PROCESS,
    SANDBOX,
    CONTAINER,
    VM,
    REMOTE_WORKER,
    GPU_WORKER,
    BROWSER,
    CUSTOM
}
