package tech.kayys.andalus.harness.observability.projection;

import tech.kayys.andalus.harness.observability.event.AndalusEvent;

import java.util.Collection;

/**
 * Universal projection interface deriving domain-specific read views from raw events.
 */
public interface EventProjection<T> {

    T project(Collection<AndalusEvent> events);
}
