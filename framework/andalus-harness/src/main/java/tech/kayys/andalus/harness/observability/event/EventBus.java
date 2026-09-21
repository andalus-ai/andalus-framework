package tech.kayys.andalus.harness.observability.event;

/**
 * Real-time event pub/sub bus decoupled from durable storage.
 */
public interface EventBus {

    void publish(AndalusEvent event);

    EventSubscription subscribe(EventFilter filter, EventHandler handler);
}
