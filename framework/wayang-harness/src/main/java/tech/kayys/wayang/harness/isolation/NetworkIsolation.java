package tech.kayys.wayang.harness.isolation;

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
