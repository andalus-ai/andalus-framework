package tech.kayys.andalus.execution.adaptation;

/**
 * Classification of execution telemetry signals that trigger adaptive evaluations.
 */
public enum SignalType {
    RESOURCE_PRESSURE,
    CAPABILITY_LOSS,
    NODE_FAILURE,
    BUDGET_EXHAUSTION,
    LATENCY_SPIKE,
    HEALTH_DEGRADATION,
    MAINTENANCE_SCHEDULED
}
