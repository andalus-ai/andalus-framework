package tech.kayys.andalus.execution.resource.binding;

/**
 * State machine for individual resource bindings.
 */
public enum ResourceBindingState {
    DECLARED,
    RESOLVING,
    PROVISIONING,
    BOUND,
    ACTIVE,
    RELEASING,
    RELEASED
}
