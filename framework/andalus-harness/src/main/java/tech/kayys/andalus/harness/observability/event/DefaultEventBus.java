package tech.kayys.andalus.harness.observability.event;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default in-memory thread-safe implementation of {@link EventBus}.
 */
public class DefaultEventBus implements EventBus {

    private final List<SubscriptionEntry> subscriptions = new CopyOnWriteArrayList<>();

    /**
     * Per-subscription active state. This must be an instance field — a static flag
     * would let one {@code unsubscribe()} silently disable every subscription in the
     * process. Unsubscribing also removes the entry so the bus does not accumulate
     * dead subscriptions.
     */
    private final class SubscriptionEntry implements EventSubscription {
        private final EventFilter filter;
        private final EventHandler handler;
        private final java.util.concurrent.atomic.AtomicBoolean active =
                new java.util.concurrent.atomic.AtomicBoolean(true);

        private SubscriptionEntry(EventFilter filter, EventHandler handler) {
            this.filter = filter;
            this.handler = handler;
        }

        @Override
        public boolean isActive() {
            return active.get();
        }

        @Override
        public void unsubscribe() {
            if (active.compareAndSet(true, false)) {
                subscriptions.remove(this);
            }
        }
    }

    @Override
    public void publish(AndalusEvent event) {
        if (event == null) return;
        for (SubscriptionEntry sub : subscriptions) {
            if (sub.isActive() && sub.filter.matches(event)) {
                try {
                    sub.handler.handle(event);
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
