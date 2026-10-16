
package tech.kayys.andalus.agent.kernel.interaction;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class CorrelatedInteractionBus
        implements InteractionBus {

    private final InteractionLedger ledger;

    private final Map<String, List<Subscription>>
            subscriptions =
            new java.util.concurrent.ConcurrentHashMap<>();

    public CorrelatedInteractionBus(
            InteractionLedger ledger
    ) {
        this.ledger = ledger;
    }

    @Override
    public InteractionEvent publish(
            String runId,
            InteractionEventType type,
            Map<String, Object> data
    ) {
        InteractionCorrelation correlation =
                extractCorrelation(data, runId);

        String requestId =
                extractRequestId(data);

        InteractionRequestType requestType =
                extractRequestType(data);

        InteractionRecord record =
                ledger.append(
                        new InteractionRecord(
                                -1,
                                requestId,
                                runId,
                                requestType,
                                type,
                                correlation,
                                Instant.now(),
                                data
                        )
                );

        InteractionEvent event =
                new InteractionEvent(
                        record.sequence(),
                        runId,
                        type,
                        record.timestamp(),
                        data
                );

        notifySubscribers(event);

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
                        afterSequence,
                        sink
                );

        subscriptions
                .computeIfAbsent(
                        runId,
                        ignored ->
                                new CopyOnWriteArrayList<>()
                )
                .add(subscription);

        return subscription;
    }

    @Override
    public List<InteractionEvent> history(
            String runId,
            long afterSequence
    ) {
        return ledger.history(
                runId,
                afterSequence
        ).stream()
                .map(record ->
                        new InteractionEvent(
                                record.sequence(),
                                record.runId(),
                                record.eventType(),
                                record.timestamp(),
                                record.data()
                        )
                )
                .toList();
    }

    private void notifySubscribers(
            InteractionEvent event
    ) {
        List<Subscription> listeners =
                subscriptions.get(
                        event.runId()
                );

        if (listeners == null) {
            return;
        }

        for (Subscription listener : listeners) {
            if (!listener.active()) {
                continue;
            }

            if (event.sequence()
                    <= listener.afterSequence) {
                continue;
            }

            listener.sink.accept(event);
            listener.afterSequence =
                    event.sequence();
        }
    }

    private InteractionCorrelation extractCorrelation(
            Map<String, Object> data,
            String runId
    ) {
        Object value =
                data.get("correlation");

        if (value instanceof InteractionCorrelation correlation) {
            return correlation;
        }

        return InteractionCorrelation.run(
                runId
        );
    }

    private String extractRequestId(
            Map<String, Object> data
    ) {
        Object value =
                data.get("requestId");

        return value == null
                ? null
                : String.valueOf(value);
    }

    private InteractionRequestType extractRequestType(
            Map<String, Object> data
    ) {
        Object value =
                data.get("requestType");

        if (value instanceof InteractionRequestType type) {
            return type;
        }

        return null;
    }

    private final class Subscription
            implements InteractionSubscription {

        private final String runId;
        private final InteractionSink sink;

        private volatile long afterSequence;
        private volatile boolean active = true;

        private Subscription(
                String runId,
                long afterSequence,
                InteractionSink sink
        ) {
            this.runId = runId;
            this.afterSequence = afterSequence;
            this.sink = sink;
        }

        @Override
        public boolean active() {
            return active;
        }

        @Override
        public void close() {
            active = false;

            List<Subscription> list =
                    subscriptions.get(runId);

            if (list != null) {
                list.remove(this);
            }
        }
    }
}
