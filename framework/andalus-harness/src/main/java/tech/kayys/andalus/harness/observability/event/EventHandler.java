package tech.kayys.andalus.harness.observability.event;

/**
 * Handler interface for receiving dispatched runtime events.
 */
@FunctionalInterface
public interface EventHandler {

    void handle(AndalusEvent event);
}
