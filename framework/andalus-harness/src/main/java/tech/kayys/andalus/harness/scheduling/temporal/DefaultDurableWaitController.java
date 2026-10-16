package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of {@link DurableWaitController} (6.15.5.3).
 *
 * <p>Enforces the critical durable-first ordering:
 * <ol>
 *   <li>Persist durable WAITING state via {@link DurableWaitCoordinator} (store + journal)</li>
 *   <li>Register transient wake trigger via {@link WakeRegistration}</li>
 * </ol>
 *
 * <p>On wake: delegates to {@link DurableWaitCoordinator} to re-read and validate durable state,
 * and clears transient registrations.</p>
 */
public final class DefaultDurableWaitController implements DurableWaitController {

    private final DurableWaitCoordinator coordinator;
    private final WakeRegistration wakeRegistration;

    public DefaultDurableWaitController(
            DurableWaitCoordinator coordinator,
            WakeRegistration wakeRegistration
    ) {
        this.coordinator = Objects.requireNonNull(coordinator, "coordinator");
        this.wakeRegistration = Objects.requireNonNull(wakeRegistration, "wakeRegistration");
    }

    @Override
    public void waitFor(ExecutionId executionId, WakeCondition condition) {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(condition, "condition");

        // 1. Persist durable WAITING state FIRST
        coordinator.enterWaiting(executionId, condition, Optional.empty());

        // 2. Register transient delivery mechanism
        wakeRegistration.register(executionId, condition);
    }

    @Override
    public void wake(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId");

        // Reloads durable state, verifies WAITING, checks deadline, transitions toward RUNNING
        WakeResult result = coordinator.wakeExecution(executionId);
        if (result == WakeResult.WOKEN || result == WakeResult.EXPIRED || result == WakeResult.IGNORED) {
            wakeRegistration.cancel(executionId);
        }
    }

    @Override
    public void cancelWake(ExecutionId executionId) {
        wakeRegistration.cancel(executionId);
    }
}
