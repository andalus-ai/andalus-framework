package tech.kayys.wayang.harness.observability.event;

import java.time.Instant;

/**
 * Universal contract for an immutable runtime event in Wayang Harness v3.2.
 */
public interface WayangEvent {

    EventId id();

    EventType type();

    EventMetadata metadata();

    EventContext context();

    EventSequence sequence();

    Instant timestamp();

    EventPayload payload();
}
