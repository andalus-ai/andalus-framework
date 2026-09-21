package tech.kayys.andalus.execution.environment;

/**
 * Supported execution environment types.
 */
public enum EnvironmentType {
    IN_PROCESS,
    LOCAL_PROCESS,
    SANDBOX,
    CONTAINER,
    VIRTUAL_MACHINE,
    WASM,
    REMOTE_WORKER,
    BROWSER,
    SPECIALIZED
}
