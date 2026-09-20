package tech.kayys.wayang.event.store;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Append-only transactional domain event representing a discrete state transition.
 */
public record DomainEvent(
    String eventId,
    String aggregateType,
    String aggregateId,
    String eventType,
    long sequenceNumber,
    Instant timestamp,
    Map<String, Object> payload
) implements Serializable {

    public DomainEvent {
        Objects.requireNonNull(eventId, "eventId cannot be null");
        Objects.requireNonNull(aggregateType, "aggregateType cannot be null");
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(eventType, "eventType cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");
        payload = payload != null ? Map.copyOf(payload) : Map.of();
    }

    public static DomainEvent create(String aggregateType, String aggregateId, String eventType, Map<String, Object> payload) {
        return new DomainEvent(
            "evt-" + UUID.randomUUID(),
            aggregateType,
            aggregateId,
            eventType,
            0L,
            Instant.now(),
            payload
        );
    }

    public DomainEvent withSequence(long sequence) {
        return new DomainEvent(
            eventId,
            aggregateType,
            aggregateId,
            eventType,
            sequence,
            timestamp,
            payload
        );
    }
}
