package tech.kayys.wayang.execution.core.control;

import tech.kayys.wayang.execution.control.ExecutionController;
import tech.kayys.wayang.execution.control.ExecutionHealthMonitor;
import tech.kayys.wayang.execution.control.ExecutionLifecycleState;
import tech.kayys.wayang.execution.control.HealthSnapshot;
import tech.kayys.wayang.execution.control.HealthStatus;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Standard implementation of {@link ExecutionHealthMonitor}.
 */
public class DefaultExecutionHealthMonitor implements ExecutionHealthMonitor {

    private final ExecutionController controller;

    public DefaultExecutionHealthMonitor(ExecutionController controller) {
        this.controller = Objects.requireNonNull(controller, "controller cannot be null");
    }

    @Override
    public HealthSnapshot inspect(String executionId) {
        Objects.requireNonNull(executionId, "executionId cannot be null");

        ExecutionLifecycleState state = controller.getState(executionId).orElse(ExecutionLifecycleState.CREATED);

        HealthStatus status;
        switch (state) {
            case RUNNING, PAUSED, CHECKPOINTED, COMPLETED -> status = HealthStatus.HEALTHY;
            case RECOVERING, MIGRATING, PAUSING, STARTING -> status = HealthStatus.DEGRADED;
            case FAILED -> status = HealthStatus.UNHEALTHY;
            case TERMINATED, CANCELLED, QUARANTINED -> status = HealthStatus.DEAD;
            default -> status = HealthStatus.HEALTHY;
        }

        return new HealthSnapshot(executionId, status, Map.of("lifecycleState", state.name()), Instant.now());
    }
}
