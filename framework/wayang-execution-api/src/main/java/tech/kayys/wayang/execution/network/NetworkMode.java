package tech.kayys.wayang.execution.network;

/**
 * Operating mode for network traffic from within the sandbox.
 */
public enum NetworkMode {
    NONE,
    LOOPBACK_ONLY,
    ALLOWLIST,
    PROXY_ONLY,
    FULL
}
