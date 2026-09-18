package tech.kayys.wayang.harness.observability.projection;

import tech.kayys.wayang.harness.observability.event.WayangEvent;

import java.util.Collection;

/**
 * Universal projection interface deriving domain-specific read views from raw events.
 */
public interface EventProjection<T> {

    T project(Collection<WayangEvent> events);
}
