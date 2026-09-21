package tech.kayys.andalus.execution.failure;

public enum FailureCategory {
    INFRASTRUCTURE,
    RESOURCE,
    POLICY,
    AUTHORIZATION,
    RUNTIME,
    CHECKPOINT,
    NETWORK,
    DEPENDENCY,
    SIDE_EFFECT,
    UNKNOWN
}
