package tech.kayys.andalus.execution.resource.model;

/**
 * Scope of ownership and sharing for a virtual resource.
 */
public enum ResourceScope {
    EXECUTION,
    WORKFLOW,
    SESSION,
    AGENT,
    SHARED,
    SYSTEM
}
