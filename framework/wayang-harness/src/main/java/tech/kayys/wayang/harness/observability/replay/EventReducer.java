package tech.kayys.wayang.harness.observability.replay;

import tech.kayys.wayang.harness.observability.event.WayangEvent;

/**
 * Functional reducer reconstructing a state representation incrementally from a sequence of events.
 */
@FunctionalInterface
public interface EventReducer<S> {

    S apply(S state, WayangEvent event);
}
