package tech.kayys.wayang.harness.resource;

/**
 * Lifecycle and allocation status of an environmental resource.
 */
public enum ResourceStatus {
    AVAILABLE,
    ALLOCATED,
    RESERVED,
    EXHAUSTED,
    RELEASED
}
