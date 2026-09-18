package tech.kayys.wayang.harness.isolation;

/**
 * Levels of filesystem isolation granted to an execution environment.
 */
public enum FilesystemIsolation {
    NONE,
    WORKSPACE,
    SANDBOX,
    READ_ONLY,
    EPHEMERAL,
    ISOLATED
}
