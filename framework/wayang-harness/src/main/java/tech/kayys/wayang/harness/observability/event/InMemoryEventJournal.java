package tech.kayys.wayang.harness.observability.event;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory thread-safe implementation of {@link EventJournal} maintaining strict per-execution sequential ordering.
 */
public class InMemoryEventJournal implements EventJournal {

    private final Map<ExecutionId, AtomicLong> sequenceCounters = new ConcurrentHashMap<>();
    private final Map<ExecutionId, List<WayangEvent>> executionEvents = new ConcurrentHashMap<>();
    private final Map<EventId, WayangEvent> eventIndex = new ConcurrentHashMap<>();

    @Override
    public synchronized EventSequence append(WayangEvent event) {
        Objects.requireNonNull(event, "event");
        ExecutionId execId = event.context().executionId();

        long nextSeq = sequenceCounters.computeIfAbsent(execId, k -> new AtomicLong(0)).incrementAndGet();
        EventSequence sequence = EventSequence.of(nextSeq);

        WayangEvent sequencedEvent = (event instanceof DefaultWayangEvent def)
                ? def.withSequence(sequence)
                : event;

        executionEvents.computeIfAbsent(execId, k -> new CopyOnWriteArrayList<>()).add(sequencedEvent);
        eventIndex.put(sequencedEvent.id(), sequencedEvent);
        return sequence;
    }

    @Override
    public Collection<WayangEvent> read(EventFilter filter) {
        EventFilter f = filter != null ? filter : EventFilter.all();
        if (f.executionId().isPresent()) {
            List<WayangEvent> events = executionEvents.getOrDefault(f.executionId().get(), List.of());
            return events.stream().filter(f::matches).toList();
        }
        return eventIndex.values().stream().filter(f::matches).toList();
    }

    @Override
    public Optional<WayangEvent> get(EventId id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(eventIndex.get(id));
    }
}
