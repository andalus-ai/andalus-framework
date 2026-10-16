package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.scheduling.scheduler.ScheduleHandle;
import tech.kayys.andalus.harness.scheduling.scheduler.ScheduleRequest;
import tech.kayys.andalus.harness.scheduling.scheduler.ScheduleSpec;
import tech.kayys.andalus.harness.scheduling.scheduler.Scheduler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Unified coordinator for all transient wake registrations (6.15.5.18).
 *
 * <p>Dispatches wake registrations according to the {@link WakeCondition} type:
 * <ul>
 *   <li>{@link WakeCondition.TimeWake} &rarr; {@link Scheduler} (6.15.4)</li>
 *   <li>{@link WakeCondition.EventWake} &rarr; {@link EventWakeRegistration} (6.15.5.5)</li>
 *   <li>{@link WakeCondition.ExecutionWake} &rarr; {@link ExecutionWakeRegistration} (6.15.5.12)</li>
 *   <li>{@link WakeCondition.HumanWake} &rarr; approval subsystem notification (6.15.5.8)</li>
 * </ul>
 *
 * <p><strong>Delivery optimization only:</strong> All registrations managed here are transient
 * delivery mechanisms. Durable execution state remains the authoritative source of truth.</p>
 */
public final class DefaultWakeCoordinator implements WakeRegistration {

    private static final Logger LOG = Logger.getLogger(DefaultWakeCoordinator.class.getName());

    private final Scheduler scheduler;
    private final EventWakeRegistration eventWake;
    private final ExecutionWakeRegistration executionWake;

    /** Tracks registered time-wake schedule handles by executionId (6.15.4.7). */
    private final Map<ExecutionId, ScheduleHandle> timeSchedules = new ConcurrentHashMap<>();

    /** Tracks last registered condition for cancellation. */
    private final Map<ExecutionId, WakeCondition> activeConditions = new ConcurrentHashMap<>();

    public DefaultWakeCoordinator(
            Scheduler scheduler,
            EventWakeRegistration eventWake,
            ExecutionWakeRegistration executionWake
    ) {
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.eventWake = Objects.requireNonNull(eventWake, "eventWake");
        this.executionWake = Objects.requireNonNull(executionWake, "executionWake");
    }

    @Override
    public void register(ExecutionId executionId, WakeCondition condition) {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(condition, "condition");

        // Cancel existing registration if present
        cancel(executionId);
        activeConditions.put(executionId, condition);

        switch (condition) {
            case WakeCondition.TimeWake timeWake ->
                    registerTime(executionId, timeWake);

            case WakeCondition.EventWake eventWakeCondition ->
                    eventWake.register(executionId, eventWakeCondition);

            case WakeCondition.ExecutionWake executionWakeCondition ->
                    executionWake.register(executionId, executionWakeCondition.targetExecutionId());

            case WakeCondition.HumanWake humanWake ->
                    registerHuman(executionId, humanWake);

            case WakeCondition.ConditionWake conditionWake ->
                    registerCondition(executionId, conditionWake);
        }
    }

    @Override
    public void cancel(ExecutionId executionId) {
        WakeCondition condition = activeConditions.remove(executionId);
        if (condition == null) {
            // Also clean up any orphan registrations
            cancelTime(executionId);
            eventWake.cancel(executionId);
            executionWake.cancelAll(executionId);
            return;
        }

        switch (condition) {
            case WakeCondition.TimeWake ignored ->
                    cancelTime(executionId);

            case WakeCondition.EventWake ignored ->
                    eventWake.cancel(executionId);

            case WakeCondition.ExecutionWake executionWakeCondition ->
                    executionWake.cancel(executionId, executionWakeCondition.targetExecutionId());

            case WakeCondition.HumanWake ignored -> {
                // Approval wake does not require local transient unregistration
            }

            case WakeCondition.ConditionWake ignored -> {
                // Condition wake does not require local transient unregistration
            }
        }
    }

    private void registerTime(ExecutionId executionId, WakeCondition.TimeWake wake) {
        ScheduleRequest request = ScheduleRequest.of(
                executionId,
                ScheduleSpec.at(wake.wakeAt())
        );
        ScheduleHandle handle = scheduler.schedule(request);
        timeSchedules.put(executionId, handle);
        LOG.fine(() -> "Registered TimeWake for " + executionId + " at " + wake.wakeAt());
    }

    private void cancelTime(ExecutionId executionId) {
        ScheduleHandle handle = timeSchedules.remove(executionId);
        if (handle != null) {
            scheduler.cancel(handle.id());
        }
    }

    private void registerHuman(ExecutionId executionId, WakeCondition.HumanWake wake) {
        LOG.fine(() -> "Registered HumanWake for " + executionId + " waiting for approval " + wake.approvalId());
    }

    /**
     * Condition wakes have no transient local delivery mechanism: the subsystem that
     * owns the condition (e.g. the A2A completion bridge for {@code a2a.task:*}
     * conditions, 6.16.4) delivers the wake through {@code DurableWaitController}.
     * Durable state remains authoritative, so a no-op registration is safe (6.15.5.18 —
     * registrations are a delivery optimization only).
     */
    private void registerCondition(ExecutionId executionId, WakeCondition.ConditionWake wake) {
        LOG.fine(() -> "ConditionWake for " + executionId + " registered as durable-only ("
                + wake.conditionExpression() + "); delivery is owned by the condition subsystem");
    }

    /** Package-private query for tests. */
    boolean hasActiveSchedule(ExecutionId executionId) {
        return timeSchedules.containsKey(executionId);
    }
}
