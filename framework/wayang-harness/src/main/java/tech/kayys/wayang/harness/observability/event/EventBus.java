package tech.kayys.wayang.harness.observability.event;

/**
 * Real-time event pub/sub bus decoupled from durable storage.
 */
public interface EventBus {

    void publish(WayangEvent event);

    EventSubscription subscribe(EventFilter filter, EventHandler handler);
}
