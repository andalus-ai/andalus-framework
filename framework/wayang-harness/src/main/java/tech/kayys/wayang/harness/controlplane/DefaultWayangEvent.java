package tech.kayys.wayang.harness.controlplane;

import tech.kayys.wayang.harness.journal.EventId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable reference record implementing {@link WayangEvent}.
 */
public record DefaultWayangEvent(
        EventId id,
        ControlPlaneEventType type,
        EventVersion version,
        Instant timestamp,
        CorrelationId correlationId,
        CausationId causationId,
        Map<String, Object> payload
) implements WayangEvent {

    public DefaultWayangEvent {
        Objects.requireNonNull(id, "EventId cannot be null");
        Objects.requireNonNull(type, "ControlPlaneEventType cannot be null");
        version = version != null ? version : EventVersion.initial();
        timestamp = timestamp != null ? timestamp : Instant.now();
        correlationId = correlationId != null ? correlationId : CorrelationId.generate();
        causationId = causationId != null ? causationId : CausationId.none();
        payload = payload != null ? Map.copyOf(payload) : Map.of();
    }

    public static DefaultWayangEvent of(
            ControlPlaneEventType type,
            CorrelationId correlationId,
            Map<String, Object> payload
    ) {
        return new DefaultWayangEvent(
                EventId.generate(),
                type,
                EventVersion.initial(),
                Instant.now(),
                correlationId,
                CausationId.none(),
                payload
        );
    }
}
