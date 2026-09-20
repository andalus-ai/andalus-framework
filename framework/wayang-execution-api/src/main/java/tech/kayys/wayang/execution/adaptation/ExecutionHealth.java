package tech.kayys.wayang.execution.adaptation;

import tech.kayys.wayang.execution.control.HealthStatus;
import tech.kayys.wayang.execution.mobility.ExecutionIdentity;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Health evaluation composite for a running execution incorporating active signals.
 */
public record ExecutionHealth(
    ExecutionIdentity executionId,
    HealthStatus status,
    List<ExecutionSignal> activeSignals,
    Instant evaluatedAt
) {
    public ExecutionHealth {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(status, "status cannot be null");
        activeSignals = activeSignals != null ? List.copyOf(activeSignals) : List.of();
        Objects.requireNonNull(evaluatedAt, "evaluatedAt cannot be null");
    }

    public static ExecutionHealth healthy(ExecutionIdentity executionId) {
        return new ExecutionHealth(executionId, HealthStatus.HEALTHY, List.of(), Instant.now());
    }

    public static ExecutionHealth degraded(ExecutionIdentity executionId, List<ExecutionSignal> signals) {
        return new ExecutionHealth(executionId, HealthStatus.DEGRADED, signals, Instant.now());
    }

    public static ExecutionHealth unhealthy(ExecutionIdentity executionId, List<ExecutionSignal> signals) {
        return new ExecutionHealth(executionId, HealthStatus.UNHEALTHY, signals, Instant.now());
    }
}
