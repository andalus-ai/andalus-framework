package tech.kayys.wayang.execution.core.control;

import tech.kayys.wayang.execution.control.CommandSource;
import tech.kayys.wayang.execution.control.ControlAction;
import tech.kayys.wayang.execution.control.ControlCommand;
import tech.kayys.wayang.execution.control.ControlResult;
import tech.kayys.wayang.execution.control.ExecutionController;
import tech.kayys.wayang.execution.control.ExecutionHealthMonitor;
import tech.kayys.wayang.execution.control.ExecutionLifecycleState;
import tech.kayys.wayang.execution.control.ExecutionPriority;
import tech.kayys.wayang.execution.control.ExecutionScheduler;
import tech.kayys.wayang.execution.control.ExecutionSupervisor;
import tech.kayys.wayang.execution.control.HealthSnapshot;
import tech.kayys.wayang.execution.control.SchedulingDecision;
import tech.kayys.wayang.execution.control.SupervisionContext;
import tech.kayys.wayang.execution.control.SupervisionDecision;
import tech.kayys.wayang.execution.control.WayangControl;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Standard implementation of {@link WayangControl} unifying execution control plane capabilities.
 */
public class DefaultWayangControl implements WayangControl {

    private final ExecutionController controller;
    private final ExecutionSupervisor supervisor;
    private final ExecutionHealthMonitor healthMonitor;
    private final ExecutionScheduler scheduler;

    public DefaultWayangControl(
            ExecutionController controller,
            ExecutionSupervisor supervisor,
            ExecutionHealthMonitor healthMonitor,
            ExecutionScheduler scheduler
    ) {
        this.controller = Objects.requireNonNull(controller, "controller cannot be null");
        this.supervisor = Objects.requireNonNull(supervisor, "supervisor cannot be null");
        this.healthMonitor = Objects.requireNonNull(healthMonitor, "healthMonitor cannot be null");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler cannot be null");
    }

    @Override
    public ControlResult pause(String executionId) {
        return controller.apply(executionId, ControlCommand.of(executionId, ControlAction.PAUSE, CommandSource.USER));
    }

    @Override
    public ControlResult resume(String executionId) {
        return controller.apply(executionId, ControlCommand.of(executionId, ControlAction.RESUME, CommandSource.USER));
    }

    @Override
    public ControlResult cancel(String executionId, String reason) {
        return controller.apply(executionId, ControlCommand.of(executionId, ControlAction.CANCEL, CommandSource.USER, Map.of("reason", reason != null ? reason : "")));
    }

    @Override
    public ControlResult checkpoint(String executionId) {
        return controller.apply(executionId, ControlCommand.of(executionId, ControlAction.CHECKPOINT, CommandSource.SYSTEM));
    }

    @Override
    public ControlResult migrate(String executionId, String targetRuntimeId) {
        return controller.apply(executionId, ControlCommand.of(executionId, ControlAction.MIGRATE, CommandSource.SYSTEM, Map.of("targetRuntimeId", targetRuntimeId)));
    }

    @Override
    public SupervisionDecision supervise(String executionId) {
        ExecutionLifecycleState state = controller.getState(executionId).orElse(ExecutionLifecycleState.CREATED);
        return supervisor.supervise(SupervisionContext.of(executionId, state));
    }

    @Override
    public HealthSnapshot inspectHealth(String executionId) {
        return healthMonitor.inspect(executionId);
    }

    @Override
    public SchedulingDecision schedule(String executionId, ExecutionPriority priority) {
        return scheduler.schedule(executionId, priority);
    }

    @Override
    public Optional<ExecutionLifecycleState> getState(String executionId) {
        return controller.getState(executionId);
    }
}
