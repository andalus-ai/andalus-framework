
package tech.kayys.andalus.agent.kernel.interaction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory interaction event bus.
 *
 * <p>Production implementations can later persist this stream
 * in the durable Agent event journal.</p>
 */
public final class InMemoryInteractionBus
        implements InteractionBus {

    private final Map<String, List<InteractionEvent>> history =
            new ConcurrentHashMap<>();

    private final Map<String, CopyOnWriteArrayList<Subscription>>
            subscribers = new ConcurrentHashMap<>();

    private final Map<String, AtomicLong> sequences =
            new ConcurrentHashMap<>();

    @Override
    public InteractionEvent publish(
            String runId,
            InteractionEventType type,
            Map<String, Object> data
    ) {
        AtomicLong counter = sequences.computeIfAbsent(
                runId,
                ignored -> new AtomicLong()
        );

        long sequence = counter.getAndIncrement();

        InteractionEvent event = new InteractionEvent(
                sequence,
                runId,
                type,
                Instant.now(),
                data
        );

        history
                .computeIfAbsent(
                        runId,
                        ignored -> new CopyOnWriteArrayList<>()
                )
                .add(event);

        List<Subscription> listeners =
                subscribers.get(runId);

        if (listeners != null) {
            for (Subscription subscription : listeners) {
                if (subscription.active()) {
                    subscription.sink.accept(event);
                }
            }
        }

        return event;
    }

    @Override
    public InteractionSubscription subscribe(
            String runId,
            long afterSequence,
            InteractionSink sink
    ) {
        Subscription subscription =
                new Subscription(
                        runId,
                        sink
                );

        subscribers
                .computeIfAbsent(
                        runId,
                        ignored -> new CopyOnWriteArrayList<>()
                )
                .add(subscription);

        /*
         * Replay existing events first.
         */
        for (InteractionEvent event :
                history(runId, afterSequence)) {

            if (subscription.active()) {
                sink.accept(event);
            }
        }

        return subscription;
    }

    @Override
    public List<InteractionEvent> history(
            String runId,
            long afterSequence
    ) {
        List<InteractionEvent> events =
                history.get(runId);

        if (events == null) {
            return List.of();
        }

        List<InteractionEvent> result =
                new ArrayList<>();

        for (InteractionEvent event : events) {
            if (event.sequence() > afterSequence) {
                result.add(event);
            }
        }

        return List.copyOf(result);
    }

    private final class Subscription
            implements InteractionSubscription {

        private final String runId;
        private final InteractionSink sink;

        private volatile boolean active = true;

        private Subscription(
                String runId,
                InteractionSink sink
        ) {
            this.runId = runId;
            this.sink = sink;
        }

        @Override
        public boolean active() {
            return active;
        }

        @Override
        public void close() {
            if (!active) {
                return;
            }

            active = false;

            CopyOnWriteArrayList<Subscription>
                    list = subscribers.get(runId);

            if (list != null) {
                list.remove(this);
            }
        }
    }
}
