package tech.kayys.wayang.harness.observability.event;

import java.util.Map;
import java.util.Objects;

/**
 * Technical metadata attached to an event including schema version, producer, sensitivity, and correlation ID.
 */
public record EventMetadata(
        String schema,
        int version,
        String producer,
        String correlationId,
        EventSensitivity sensitivity,
        EventDurability durability,
        Map<String, String> tags
) {
    public EventMetadata {
        Objects.requireNonNull(schema, "schema");
        producer = producer != null ? producer : "wayang-harness";
        correlationId = correlationId != null ? correlationId : "corr-default";
        sensitivity = sensitivity != null ? sensitivity : EventSensitivity.INTERNAL;
        durability = durability != null ? durability : EventDurability.DURABLE;
        tags = tags != null ? Map.copyOf(tags) : Map.of();
    }

    public static EventMetadata of(String schema, int version) {
        return new EventMetadata(schema, version, "wayang-harness", "corr-default", EventSensitivity.INTERNAL, EventDurability.DURABLE, Map.of());
    }
}
