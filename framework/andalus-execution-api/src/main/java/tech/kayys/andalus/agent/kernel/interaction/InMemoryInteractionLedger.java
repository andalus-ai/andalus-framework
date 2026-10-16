
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class InMemoryInteractionLedger
        implements InteractionLedger {

    private final Map<String, List<InteractionRecord>> records =
            new ConcurrentHashMap<>();

    private final Map<String, AtomicLong> sequences =
            new ConcurrentHashMap<>();

    @Override
    public InteractionRecord append(
            InteractionRecord record
    ) {
        String runId =
                record.runId();

        AtomicLong counter =
                sequences.computeIfAbsent(
                        runId,
                        ignored -> new AtomicLong(-1)
                );

        long sequence =
                counter.incrementAndGet();

        InteractionRecord persisted =
                new InteractionRecord(
                        sequence,
                        record.requestId(),
                        record.runId(),
                        record.type(),
                        record.eventType(),
                        record.correlation(),
                        record.timestamp(),
                        record.data()
                );

        records.computeIfAbsent(
                runId,
                ignored -> new ArrayList<>()
        ).add(persisted);

        return persisted;
    }

    @Override
    public List<InteractionRecord> history(
            String runId,
            long afterSequence
    ) {
        return records.getOrDefault(
                runId,
                List.of()
        ).stream()
                .filter(r ->
                        r.sequence() > afterSequence
                )
                .toList();
    }

    @Override
    public List<InteractionRecord> historyForRequest(
            String requestId
    ) {
        return records.values()
                .stream()
                .flatMap(List::stream)
                .filter(r ->
                        requestId.equals(
                                r.requestId()
                        )
                )
                .toList();
    }

    @Override
    public long latestSequence(
            String runId
    ) {
        AtomicLong counter =
                sequences.get(runId);

        return counter == null
                ? -1
                : counter.get();
    }
}
