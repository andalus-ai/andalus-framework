package tech.kayys.wayang.execution.control;

import java.util.Optional;

/**
 * Unified facade for the Wayang Execution Supervision & Control Plane (v4.34).
 */
public interface WayangControl {

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
