package tech.kayys.wayang.harness.observability.event;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default in-memory thread-safe implementation of {@link EventBus}.
 */
public class DefaultEventBus implements EventBus {

    private final List<SubscriptionEntry> subscriptions = new CopyOnWriteArrayList<>();

    private record SubscriptionEntry(EventFilter filter, EventHandler handler) implements EventSubscription {
        private static final java.util.concurrent.atomic.AtomicBoolean active = new java.util.concurrent.atomic.AtomicBoolean(true);

        @Override
        public boolean isActive() {
            return active.get();
        }

        @Override
        public void unsubscribe() {
            active.set(false);
        }
    }

    @Override
    public void publish(WayangEvent event) {
        if (event == null) return;
        for (SubscriptionEntry sub : subscriptions) {
            if (sub.isActive() && sub.filter().matches(event)) {
                try {
                    sub.handler().handle(event);
                } catch (Exception ignored) {
                    // Prevent faulty subscriber from interrupting event dispatch
                }
            }
        }
    }

    @Override
    public EventSubscription subscribe(EventFilter filter, EventHandler handler) {
        Objects.requireNonNull(handler, "handler");
        EventFilter f = filter != null ? filter : EventFilter.all();
        SubscriptionEntry entry = new SubscriptionEntry(f, handler);
        subscriptions.add(entry);
        return entry;
    }
}
