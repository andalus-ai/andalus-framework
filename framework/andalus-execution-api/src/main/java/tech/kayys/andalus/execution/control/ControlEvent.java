package tech.kayys.andalus.execution.control;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when an execution undergoes a validated state transition.
 */
public record ControlEvent(
        EventId id,
        String executionId,
        ExecutionLifecycleState previous,
        ExecutionLifecycleState current,
        ControlAction action,
        Instant occurredAt
) {
    public ControlEvent {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(previous, "previous cannot be null");
        Objects.requireNonNull(current, "current cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    public static ControlEvent of(String executionId, ExecutionLifecycleState prev, ExecutionLifecycleState cur, ControlAction action) {
        return new ControlEvent(EventId.generate(), executionId, prev, cur, action, Instant.now());
    }
}
