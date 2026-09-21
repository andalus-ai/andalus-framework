package tech.kayys.andalus.execution.core.control;

import tech.kayys.andalus.execution.control.CommandSource;
import tech.kayys.andalus.execution.control.ControlAction;
import tech.kayys.andalus.execution.control.ControlCommand;
import tech.kayys.andalus.execution.control.ControlResult;
import tech.kayys.andalus.execution.control.ExecutionController;
import tech.kayys.andalus.execution.control.ExecutionHealthMonitor;
import tech.kayys.andalus.execution.control.ExecutionLifecycleState;
import tech.kayys.andalus.execution.control.ExecutionPriority;
import tech.kayys.andalus.execution.control.ExecutionScheduler;
import tech.kayys.andalus.execution.control.ExecutionSupervisor;
import tech.kayys.andalus.execution.control.HealthSnapshot;
import tech.kayys.andalus.execution.control.SchedulingDecision;
import tech.kayys.andalus.execution.control.SupervisionContext;
import tech.kayys.andalus.execution.control.SupervisionDecision;
import tech.kayys.andalus.execution.control.AndalusControl;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Standard implementation of {@link AndalusControl} unifying execution control plane capabilities.
 */
public class DefaultAndalusControl implements AndalusControl {

    private final ExecutionController controller;
    private final ExecutionSupervisor supervisor;
    private final ExecutionHealthMonitor healthMonitor;
    private final ExecutionScheduler scheduler;

    public DefaultAndalusControl(
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
