package tech.kayys.wayang.harness.controlplane;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread-safe reference implementation of {@link ControlPlaneEventBus}.
 */
public class DefaultControlPlaneEventBus implements ControlPlaneEventBus {

    private final Map<StreamId, List<ControlPlaneEventHandler>> streamHandlers = new ConcurrentHashMap<>();
    private final List<ControlPlaneEventHandler> globalHandlers = new CopyOnWriteArrayList<>();

    @Override
    public void publish(StreamId streamId, EventEnvelope envelope) {
        Objects.requireNonNull(streamId, "StreamId cannot be null");
        Objects.requireNonNull(envelope, "EventEnvelope cannot be null");

        // Dispatch to stream-specific subscribers
        List<ControlPlaneEventHandler> specific = streamHandlers.get(streamId);
        if (specific != null) {
            for (ControlPlaneEventHandler handler : specific) {
                try {
                    handler.onEvent(envelope);
                } catch (Exception e) {
                    // Prevent single handler failure from halting bus delivery
                }
            }
        }

        // Dispatch to global subscribers
        for (ControlPlaneEventHandler handler : globalHandlers) {
            try {
                handler.onEvent(envelope);
            } catch (Exception e) {
                // Prevent single handler failure from halting bus delivery
            }
        }
    }

    @Override
    public EventSubscription subscribe(StreamId streamId, ControlPlaneEventHandler handler) {
        Objects.requireNonNull(streamId, "StreamId cannot be null");
        Objects.requireNonNull(handler, "Handler cannot be null");

        List<ControlPlaneEventHandler> list = streamHandlers.computeIfAbsent(streamId, k -> new CopyOnWriteArrayList<>());
        list.add(handler);

        AtomicBoolean active = new AtomicBoolean(true);
        return new EventSubscription() {
            @Override
            public void cancel() {
                if (active.compareAndSet(true, false)) {
                    list.remove(handler);
                }
            }

            @Override
            public boolean isActive() {
                return active.get();
            }
        };
    }

    @Override
    public EventSubscription subscribeAll(ControlPlaneEventHandler handler) {
        Objects.requireNonNull(handler, "Handler cannot be null");
        globalHandlers.add(handler);

        AtomicBoolean active = new AtomicBoolean(true);
        return new EventSubscription() {
            @Override
            public void cancel() {
                if (active.compareAndSet(true, false)) {
                    globalHandlers.remove(handler);
                }
            }

            @Override
            public boolean isActive() {
                return active.get();
            }
        };
    }
}
