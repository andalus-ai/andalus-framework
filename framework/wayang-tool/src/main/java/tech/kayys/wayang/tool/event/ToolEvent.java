package tech.kayys.wayang.tool.event;

import tech.kayys.wayang.tool.ToolInvocationId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ToolEvent(
        String eventId,
        ToolInvocationId invocationId,
        ToolEventType type,
        Map<String, Object> payload,
        Instant timestamp
) {
    public ToolEvent {
        Objects.requireNonNull(eventId, "eventId cannot be null");
        Objects.requireNonNull(type, "ToolEventType cannot be null");
        payload = payload != null ? Map.copyOf(payload) : Map.of();
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    public static ToolEvent of(ToolInvocationId invocationId, ToolEventType type, Map<String, Object> payload) {
        return new ToolEvent("tevt-" + UUID.randomUUID(), invocationId, type, payload, Instant.now());
    }

    public static ToolEvent of(ToolInvocationId invocationId, ToolEventType type) {
        return of(invocationId, type, Map.of());
    }
}
