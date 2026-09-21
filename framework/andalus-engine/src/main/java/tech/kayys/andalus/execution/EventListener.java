package tech.kayys.andalus.execution;

/**
 * Event listener.
 */
@FunctionalInterface
public interface EventListener {

    /**
     * Handles an event.
     */
    void onEvent(Event event);
}