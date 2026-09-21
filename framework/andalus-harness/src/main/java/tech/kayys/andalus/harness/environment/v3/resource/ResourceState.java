package tech.kayys.andalus.harness.environment.v3.resource;

/**
 * Operating state of an environmental resource.
 */
public enum ResourceState {
    AVAILABLE,
    ALLOCATED,
    BUSY,
    DRAINING,
    DEGRADED,
    OFFLINE
}
