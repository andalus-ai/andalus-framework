package tech.kayys.andalus.observability.analytics;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * In-memory reference implementation of {@link AnalyticsStore} for testing and lightweight community deployments.
 */
public class InMemoryAnalyticsStore implements AnalyticsStore {

    private final List<AnalyticsRecord> records = new CopyOnWriteArrayList<>();

    @Override
    public CompletionStage<Void> write(AnalyticsRecord record) {
        if (record != null) {
            records.add(record);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<List<AnalyticsRecord>> query(AnalyticsQuery query) {
        if (query == null) {
            return CompletableFuture.completedFuture(List.copyOf(records));
        }

        List<AnalyticsRecord> filtered = records.stream()
                .filter(r -> query.from() == null || !r.timestamp().isBefore(query.from()))
                .filter(r -> query.until() == null || !r.timestamp().isAfter(query.until()))
                .limit(query.limit())
                .collect(Collectors.toList());

        return CompletableFuture.completedFuture(filtered);
    }
}
