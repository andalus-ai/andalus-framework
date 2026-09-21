package tech.kayys.andalus.harness.observability.event;

import java.util.Collection;
import java.util.Optional;

/**
 * Append-only durable event history for an execution and the harness.
 */
public interface EventJournal {

    EventSequence append(AndalusEvent event);

    Collection<AndalusEvent> read(EventFilter filter);

    Optional<AndalusEvent> get(EventId id);
}
