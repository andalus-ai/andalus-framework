package tech.kayys.andalus.execution.control;

import java.util.Map;
import java.util.Objects;

/**
 * Context provided to an ExecutionSupervisor to decide on remediation or lifecycle commands.
 */
public record SupervisionContext(
        String executionId,
        ExecutionLifecycleState state,
        int consecutiveFailures,
        long elapsedTimeMillis,
        Map<String, Object> metrics
) {
    public SupervisionContext {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(state, "state cannot be null");
        metrics = metrics == null ? Map.of() : Map.copyOf(metrics);
    }

    public static SupervisionContext of(String executionId, ExecutionLifecycleState state) {
        return new SupervisionContext(executionId, state, 0, 0L, Map.of());
    }
}
