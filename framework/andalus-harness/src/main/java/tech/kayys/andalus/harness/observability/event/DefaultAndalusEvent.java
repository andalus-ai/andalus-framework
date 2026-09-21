package tech.kayys.andalus.harness.observability.event;

import java.time.Instant;
import java.util.Objects;

/**
 * Default record implementation of {@link AndalusEvent}.
 */
public record DefaultAndalusEvent(
        EventId id,
        EventType type,
        EventMetadata metadata,
        EventContext context,
        EventSequence sequence,
        Instant timestamp,
        EventPayload payload
) implements AndalusEvent {
    public DefaultAndalusEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(context, "context");
        sequence = sequence != null ? sequence : EventSequence.of(0L);
        timestamp = timestamp != null ? timestamp : Instant.now();
        payload = payload != null ? payload : EventPayload.empty();
    }

    public static DefaultAndalusEvent of(EventType type, EventContext context, EventPayload payload) {
        return new DefaultAndalusEvent(
                EventId.generate(),
                type,
                EventMetadata.of(type.canonicalName(), 1),
                context,
                EventSequence.of(0L),
                Instant.now(),
                payload
        );
    }

    public DefaultAndalusEvent withSequence(EventSequence seq) {
        return new DefaultAndalusEvent(id, type, metadata, context, seq, timestamp, payload);
    }
}
