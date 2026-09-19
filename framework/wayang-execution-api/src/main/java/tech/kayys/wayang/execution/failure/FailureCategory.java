package tech.kayys.wayang.execution.failure;

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
