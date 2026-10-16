package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.scheduling.scheduler.DefaultScheduler;
import tech.kayys.andalus.harness.scheduling.scheduler.ScheduleHandle;

import java.util.Objects;

/**
 * Delivers due scheduler entries to the durable wait controller (6.15.4.3).
 *
 * <p>The scheduler is only a delivery mechanism. Durable execution state
 * remains the source of truth.</p>
 */
public final class TimeWakeDispatcher {

    private final DefaultScheduler scheduler;
    private final DurableWaitController waitController;

    public TimeWakeDispatcher(
            DefaultScheduler scheduler,
            DurableWaitController waitController
    ) {
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.waitController = Objects.requireNonNull(waitController, "waitController");
    }

    /**
     * Polls and claims due schedules from the scheduler, waking their respective executions.
     */
    public void dispatchDue() {
        for (ScheduleHandle handle : scheduler.claimDue()) {
            dispatch(handle);
        }
    }

    private void dispatch(ScheduleHandle handle) {
        ExecutionId executionId = handle.executionId();
        /*
         * wake() is deliberately responsible for re-reading durable state.
         *
         * Therefore:
         *   - stale schedule -> no-op
         *   - canceled execution -> no-op
         *   - completed execution -> no-op
         *   - duplicate delivery -> no-op
         *
         * The scheduler does not decide whether the execution is still
         * eligible to wake.
         */
        waitController.wake(executionId);
    }
}
