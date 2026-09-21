package tech.kayys.andalus.execution.control;

/**
 * Health states for supervised executions and runtimes.
 */
public enum HealthStatus {
    HEALTHY,
    DEGRADED,
    STALLED,
    UNHEALTHY,
    DEAD
}
