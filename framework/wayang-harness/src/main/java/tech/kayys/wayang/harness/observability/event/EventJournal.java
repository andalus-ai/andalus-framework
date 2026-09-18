package tech.kayys.wayang.harness.observability.event;

import java.util.Collection;
import java.util.Optional;

/**
 * Append-only durable event history for an execution and the harness.
 */
public interface EventJournal {

    EventSequence append(WayangEvent event);

    Collection<WayangEvent> read(EventFilter filter);

    Optional<WayangEvent> get(EventId id);
}
