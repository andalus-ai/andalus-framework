package tech.kayys.wayang.harness.controlplane;

/**
 * Pub/Sub event bus coordinating decoupled control-plane subscribers and dispatchers.
 */
public interface ControlPlaneEventBus {

    void publish(StreamId streamId, EventEnvelope envelope);

    EventSubscription subscribe(StreamId streamId, ControlPlaneEventHandler handler);

    EventSubscription subscribeAll(ControlPlaneEventHandler handler);
}
