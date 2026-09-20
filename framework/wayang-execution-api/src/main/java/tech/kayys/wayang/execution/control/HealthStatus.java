package tech.kayys.wayang.execution.control;

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
