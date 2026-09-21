package tech.kayys.andalus.harness.isolation;

/**
 * Levels of network isolation granted to an execution environment.
 */
public enum NetworkIsolation {
    NONE,
    DISABLED,
    LOOPBACK_ONLY,
    ALLOWLIST,
    RESTRICTED,
    FULL
}
