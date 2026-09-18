package tech.kayys.wayang.harness.observability.event;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.time.Instant;
import java.util.Optional;

/**
 * Filter specification for querying the event journal or filtering real-time event streams.
 */
public record EventFilter(
        Optional<ExecutionId> executionId,
        Optional<EventDomain> domain,
        Optional<String> action,
        Optional<Instant> fromTimestamp,
        Optional<Instant> toTimestamp,
        Optional<EventSequence> afterSequence
) {
    public static EventFilter all() {
        return new EventFilter(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static EventFilter forExecution(ExecutionId executionId) {
        return new EventFilter(Optional.ofNullable(executionId), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static EventFilter forDomain(EventDomain domain) {
        return new EventFilter(Optional.empty(), Optional.ofNullable(domain), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public boolean matches(WayangEvent event) {
        if (event == null) return false;
        if (executionId.isPresent() && !executionId.get().equals(event.context().executionId())) return false;
        if (domain.isPresent() && !domain.get().equals(event.type().domain())) return false;
        if (action.isPresent() && !action.get().equalsIgnoreCase(event.type().action())) return false;
        if (fromTimestamp.isPresent() && event.timestamp().isBefore(fromTimestamp.get())) return false;
        if (toTimestamp.isPresent() && event.timestamp().isAfter(toTimestamp.get())) return false;
        if (afterSequence.isPresent() && event.sequence().compareTo(afterSequence.get()) <= 0) return false;
        return true;
    }
}
