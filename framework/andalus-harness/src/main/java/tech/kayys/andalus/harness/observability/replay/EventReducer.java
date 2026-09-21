package tech.kayys.andalus.harness.observability.replay;

import tech.kayys.andalus.harness.observability.event.AndalusEvent;

/**
 * Functional reducer reconstructing a state representation incrementally from a sequence of events.
 */
@FunctionalInterface
public interface EventReducer<S> {

    S apply(S state, AndalusEvent event);
}
