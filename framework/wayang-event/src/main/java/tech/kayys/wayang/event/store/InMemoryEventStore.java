package tech.kayys.wayang.event.store;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe in-memory implementation of {@link EventStoreSPI}.
 */
public class InMemoryEventStore implements EventStoreSPI {

    private final Map<String, List<DomainEvent>> aggregateEvents = new ConcurrentHashMap<>();
    private final List<DomainEvent> globalLog = new CopyOnWriteArrayList<>();

    @Override
    public DomainEvent append(DomainEvent event) {
        Objects.requireNonNull(event, "event cannot be null");
        String aggKey = event.aggregateType() + ":" + event.aggregateId();

        List<DomainEvent> list = aggregateEvents.computeIfAbsent(aggKey, k -> new CopyOnWriteArrayList<>());
        synchronized (list) {
            long nextSeq = list.size() + 1L;
            DomainEvent sequenced = event.withSequence(nextSeq);
            list.add(sequenced);
            globalLog.add(sequenced);
            return sequenced;
        }
    }

    @Override
    public List<DomainEvent> getByAggregate(String aggregateType, String aggregateId, long fromSequence) {
        Objects.requireNonNull(aggregateType, "aggregateType cannot be null");
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");

        String aggKey = aggregateType + ":" + aggregateId;
        List<DomainEvent> list = aggregateEvents.get(aggKey);
        if (list == null || list.isEmpty()) {
            return List.of();
        }

        List<DomainEvent> result = new ArrayList<>();
        for (DomainEvent event : list) {
            if (event.sequenceNumber() >= fromSequence) {
                result.add(event);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public List<DomainEvent> getByType(String eventType, Instant fromTime) {
        Objects.requireNonNull(eventType, "eventType cannot be null");
        Objects.requireNonNull(fromTime, "fromTime cannot be null");

        List<DomainEvent> result = new ArrayList<>();
        for (DomainEvent event : globalLog) {
            if (eventType.equals(event.eventType()) && !event.timestamp().isBefore(fromTime)) {
                result.add(event);
            }
        }
        return Collections.unmodifiableList(result);
    }
}
