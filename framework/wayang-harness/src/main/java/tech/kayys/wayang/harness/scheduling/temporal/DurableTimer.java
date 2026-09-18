package tech.kayys.wayang.harness.scheduling.temporal;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Durable timer definition associated with an execution.
 */
public record DurableTimer(
        String id,
        ExecutionId executionId,
        Instant fireAt,
        TimerState state
) {
    public enum TimerState {
        PENDING,
        FIRED,
        CANCELED
    }

    public DurableTimer {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(fireAt, "fireAt");
        state = state != null ? state : TimerState.PENDING;
    }

    public static DurableTimer of(ExecutionId executionId, Instant fireAt) {
        return new DurableTimer("tmr-" + UUID.randomUUID(), executionId, fireAt, TimerState.PENDING);
    }
}
