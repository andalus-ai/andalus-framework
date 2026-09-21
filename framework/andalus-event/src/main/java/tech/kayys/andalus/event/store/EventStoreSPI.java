package tech.kayys.andalus.event.store;

import java.time.Instant;
import java.util.List;

/**
 * Service Provider Interface for the append-only event store.
 */
public interface EventStoreSPI {

    /**
     * Appends an event to the store, assigning the next sequence number for the aggregate.
     */
    DomainEvent append(DomainEvent event);

    /**
     * Retrieves ordered events for a specific aggregate starting from a sequence number.
     */
    List<DomainEvent> getByAggregate(String aggregateType, String aggregateId, long fromSequence);

    /**
     * Retrieves events of a given type that occurred after the given timestamp.
     */
    List<DomainEvent> getByType(String eventType, Instant fromTime);
}
