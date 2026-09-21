package tech.kayys.andalus.execution.resource.lifecycle;

/**
 * Health assessment of a bound virtual resource.
 */
public enum ResourceHealth {
    HEALTHY,
    DEGRADED,
    EXHAUSTED,
    FAILED,
    UNKNOWN
}
