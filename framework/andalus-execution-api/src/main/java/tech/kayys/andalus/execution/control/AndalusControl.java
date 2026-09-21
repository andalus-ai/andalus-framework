package tech.kayys.andalus.execution.control;

import java.util.Optional;

/**
 * Unified facade for the Andalus Execution Supervision & Control Plane (v4.34).
 */
public interface AndalusControl {

    ControlResult pause(String executionId);

    ControlResult resume(String executionId);

    ControlResult cancel(String executionId, String reason);

    ControlResult checkpoint(String executionId);

    ControlResult migrate(String executionId, String targetRuntimeId);

    SupervisionDecision supervise(String executionId);

    HealthSnapshot inspectHealth(String executionId);

    SchedulingDecision schedule(String executionId, ExecutionPriority priority);

    Optional<ExecutionLifecycleState> getState(String executionId);
}
