package tech.kayys.andalus.harness.observability.event;

import java.time.Instant;

/**
 * Universal contract for an immutable runtime event in Andalus Harness v3.2.
 */
public interface AndalusEvent {

    EventId id();

    EventType type();

    EventMetadata metadata();

    EventContext context();

    EventSequence sequence();

    Instant timestamp();

    EventPayload payload();
}
