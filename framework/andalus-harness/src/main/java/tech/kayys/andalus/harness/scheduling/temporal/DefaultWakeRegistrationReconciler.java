package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionStateStore;
import tech.kayys.andalus.harness.execution.state.ExecutionStatus;
import tech.kayys.andalus.harness.scheduling.clock.AndalusClock;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Default implementation of {@link WakeRegistrationReconciler} (6.15.6).
 *
 * <p>After crash or restart, reconciles transient wake mechanisms for executions that
 * are durably {@link ExecutionStatus#WAITING}:
 * <ul>
 *   <li>{@link WakeCondition.TimeWake}: if due time has passed, wakes immediately; otherwise schedules it (6.15.6.6)</li>
 *   <li>{@link WakeCondition.EventWake}: re-establishes EventBus subscription (6.15.6.7)</li>
 *   <li>{@link WakeCondition.ExecutionWake}: inspects target execution durable state; if already terminal, wakes immediately; otherwise registers completion listener with register-recheck pattern (6.15.6.11)</li>
 *   <li>{@link WakeCondition.HumanWake}: preserves waiting state until approval is resolved (6.15.6.9)</li>
 * </ul>
 *
 * <p><strong>Invariants (6.15.6.5, 6.15.6.17):</strong>
 * <ul>
 *   <li>Reconciler never changes an execution to WAITING — execution must already be WAITING</li>
 *   <li>Transient registrations are never the source of truth</li>
 *   <li>Safe against stale wakes and duplicate deliveries</li>
 * </ul>
 */
public class DefaultWakeRegistrationReconciler implements WakeRegistrationReconciler {

    private static final Logger LOG = Logger.getLogger(DefaultWakeRegistrationReconciler.class.getName());

    private final ExecutionStateStore stateStore;
    private final DurableWaitController waitController;
    private final WakeRegistration wakeRegistration;
    private final AndalusClock clock;
    private final Function<ExecutionId, Optional<WakeCondition>> conditionResolver;

    public DefaultWakeRegistrationReconciler(
            ExecutionStateStore stateStore,
            DurableWaitController waitController,
            WakeRegistration wakeRegistration,
            AndalusClock clock,
            Function<ExecutionId, Optional<WakeCondition>> conditionResolver
    ) {
        this.stateStore = Objects.requireNonNull(stateStore, "stateStore");
        this.waitController = Objects.requireNonNull(waitController, "waitController");
        this.wakeRegistration = Objects.requireNonNull(wakeRegistration, "wakeRegistration");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.conditionResolver = Objects.requireNonNull(conditionResolver, "conditionResolver");
    }

    @Override
    public void reconcile(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId");

        // 1. Load durable state
        Optional<ExecutionState> stateOpt = stateStore.load(executionId);
        if (stateOpt.isEmpty()) {
            LOG.fine(() -> "Reconcile skipped: execution not found: " + executionId);
            return;
        }

        ExecutionState state = stateOpt.get();
        // 2. Only WAITING executions are eligible for wake reconstruction (6.15.6.5)
        if (state.status() != ExecutionStatus.WAITING) {
            LOG.fine(() -> "Reconcile skipped: execution " + executionId + " is not WAITING (" + state.status() + ")");
            return;
        }

        // 3. Resolve durable wake condition
        Optional<WakeCondition> conditionOpt = conditionResolver.apply(executionId);
        if (conditionOpt.isEmpty()) {
            LOG.warning(() -> "Reconcile: no WakeCondition found for WAITING execution " + executionId);
            return;
        }

        WakeCondition condition = conditionOpt.get();
        reconcileCondition(executionId, condition);
    }

    private void reconcileCondition(ExecutionId executionId, WakeCondition condition) {
        switch (condition) {
            case WakeCondition.TimeWake timeWake ->
                    reconcileTimeWake(executionId, timeWake);

            case WakeCondition.EventWake eventWake ->
                    reconcileEventWake(executionId, eventWake);

            case WakeCondition.ExecutionWake execWake ->
                    reconcileExecutionWake(executionId, execWake);

            case WakeCondition.HumanWake humanWake ->
                    reconcileHumanWake(executionId, humanWake);

            case WakeCondition.ConditionWake conditionWake ->
                    reconcileConditionWake(executionId, conditionWake);
        }
    }

    private void reconcileTimeWake(ExecutionId executionId, WakeCondition.TimeWake timeWake) {
        Instant now = clock.now();
        if (!timeWake.wakeAt().isAfter(now)) {
            // Missed timer while down — wake immediately (6.15.6.6)
            LOG.info(() -> "TimeWake for " + executionId + " was due at " + timeWake.wakeAt() + " (now=" + now + ") -> waking immediately");
            waitController.wake(executionId);
        } else {
            // Future timer — re-register with scheduler (6.15.6.6)
            LOG.info(() -> "TimeWake for " + executionId + " due at " + timeWake.wakeAt() + " -> re-registering");
            wakeRegistration.register(executionId, timeWake);
        }
    }

    private void reconcileEventWake(ExecutionId executionId, WakeCondition.EventWake eventWake) {
        LOG.info(() -> "Re-subscribing EventWake for " + executionId);
        wakeRegistration.register(executionId, eventWake);
    }

    private void reconcileExecutionWake(ExecutionId executionId, WakeCondition.ExecutionWake execWake) {
        ExecutionId targetId = execWake.targetExecutionId();
        Optional<ExecutionState> targetOpt = stateStore.load(targetId);

        if (targetOpt.isPresent() && targetOpt.get().status().isTerminal()) {
            // Target completed while down -> wake immediately (6.15.6.10)
            LOG.info(() -> "Target execution " + targetId + " is already terminal (" + targetOpt.get().status() + ") -> waking waiter " + executionId);
            waitController.wake(executionId);
            return;
        }

        // Register-recheck pattern to prevent race between completion and registration (6.15.6.11)
        wakeRegistration.register(executionId, execWake);

        // Recheck target state in case it completed during registration
        Optional<ExecutionState> recheck = stateStore.load(targetId);
        if (recheck.isPresent() && recheck.get().status().isTerminal()) {
            LOG.info(() -> "Target execution " + targetId + " completed during re-registration -> waking waiter " + executionId);
            waitController.wake(executionId);
        }
    }

    private void reconcileHumanWake(ExecutionId executionId, WakeCondition.HumanWake humanWake) {
        LOG.info(() -> "HumanWake for " + executionId + " waiting on approval " + humanWake.approvalId() + " -> remains WAITING");
        wakeRegistration.register(executionId, humanWake);
    }

    private void reconcileConditionWake(ExecutionId executionId, WakeCondition.ConditionWake conditionWake) {
        LOG.fine(() -> "ConditionWake reconciliation deferred");
    }
}
