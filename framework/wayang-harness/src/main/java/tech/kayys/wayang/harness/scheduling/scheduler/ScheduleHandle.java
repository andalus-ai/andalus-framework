package tech.kayys.wayang.harness.scheduling.scheduler;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.time.Instant;
import java.util.Objects;

/**
 * Handle to an active schedule.
 */
public record ScheduleHandle(
        ScheduleId id,
        ExecutionId executionId,
        ScheduleState state,
        Instant scheduledTime
) {
    public ScheduleHandle {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(scheduledTime, "scheduledTime");
    }
}
