package tech.kayys.wayang.execution.resource.model;

/**
 * Declares the lifetime of a resource binding.
 */
public enum ResourceLifetime {
    EPHEMERAL,
    TASK,
    EXECUTION,
    PERSISTENT
}
