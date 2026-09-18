package tech.kayys.wayang.harness.observability.event;

/**
 * Handler interface for receiving dispatched runtime events.
 */
@FunctionalInterface
public interface EventHandler {

    void handle(WayangEvent event);
}
