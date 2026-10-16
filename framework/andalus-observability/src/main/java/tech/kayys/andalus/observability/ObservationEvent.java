package tech.kayys.andalus.observability;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ObservationEvent(
        String id,
        String executionId,
        String traceId,
        String type,
        Instant timestamp,
        Map<String, Object> attributes
) {

    public ObservationEvent {
        id = id == null || id.isBlank()
                ? "event-" + UUID.randomUUID()
                : id;

        Objects.requireNonNull(executionId,
                "executionId must not be null");

        Objects.requireNonNull(traceId,
                "traceId must not be null");

        Objects.requireNonNull(type,
                "type must not be null");

        timestamp = timestamp == null
                ? Instant.now()
                : timestamp;

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
