package tech.kayys.andalus.observability.analytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation of {@link AnalyticsRuntime} aggregating telemetry and delegating to analyzers.
 */
public class DefaultAnalyticsRuntime implements AnalyticsRuntime {

    private final AnalyticsStore store;
    private final List<AnalyticsAnalyzer> analyzers = new CopyOnWriteArrayList<>();

    public DefaultAnalyticsRuntime(AnalyticsStore store) {
        this.store = Objects.requireNonNullElseGet(store, InMemoryAnalyticsStore::new);
    }

    public DefaultAnalyticsRuntime(AnalyticsStore store, List<AnalyticsAnalyzer> initialAnalyzers) {
        this.store = Objects.requireNonNullElseGet(store, InMemoryAnalyticsStore::new);
        if (initialAnalyzers != null) {
            this.analyzers.addAll(initialAnalyzers);
        }
    }

    public void registerAnalyzer(AnalyticsAnalyzer analyzer) {
        if (analyzer != null) {
            analyzers.add(analyzer);
        }
    }

    @Override
    public CompletionStage<AnalyticsReport> analyze(AnalyticsQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return insights(query).thenApply(insightsList ->
                new AnalyticsReport(
                        query,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        List.of(),
                        insightsList,
                        java.util.Map.of("analyzersRun", analyzers.size())
                )
        );
    }

    @Override
    public CompletionStage<List<AnalyticsInsight>> insights(AnalyticsQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        List<CompletableFuture<List<AnalyticsInsight>>> futures = analyzers.stream()
                .map(a -> a.analyze(query).toCompletableFuture())
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<AnalyticsInsight> all = new ArrayList<>();
                    for (var f : futures) {
                        all.addAll(f.join());
                    }
                    return all;
                });
    }
}
