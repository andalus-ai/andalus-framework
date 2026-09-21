package tech.kayys.andalus.execution.resource.model;

/**
 * Declares the lifetime of a resource binding.
 */
public enum ResourceLifetime {
    EPHEMERAL,
    TASK,
    EXECUTION,
    PERSISTENT
}
