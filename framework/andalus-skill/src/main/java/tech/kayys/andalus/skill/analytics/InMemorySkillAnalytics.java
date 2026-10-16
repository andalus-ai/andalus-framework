package tech.kayys.andalus.skill.analytics;

import tech.kayys.andalus.skill.observability.InMemorySkillObservationRecorder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * In-memory analytics engine calculating statistics and insights from
 * an {@link InMemorySkillObservationRecorder}.
 */
public final class InMemorySkillAnalytics implements SkillAnalytics {

    private final InMemorySkillObservationRecorder recorder;

    public InMemorySkillAnalytics(InMemorySkillObservationRecorder recorder) {
        this.recorder = Objects.requireNonNull(recorder, "recorder must not be null");
    }

    @Override
    public CompletionStage<SkillStatistics> statistics(SkillAnalyticsQuery query) {
        List<InMemorySkillObservationRecorder.ExecutionRecord> records = filterRecords(query);

        if (records.isEmpty()) {
            return CompletableFuture.completedFuture(SkillStatistics.empty());
        }

        long executions = records.size();
        long successes = records.stream().filter(InMemorySkillObservationRecorder.ExecutionRecord::isSuccess).count();
        long failures = executions - successes;

        List<Long> durations = records.stream()
                .map(r -> r.duration().toMillis())
                .sorted()
                .collect(Collectors.toList());

        long sumDuration = durations.stream().mapToLong(Long::longValue).sum();
        Duration avg = Duration.ofMillis(sumDuration / executions);
        Duration p50 = Duration.ofMillis(percentile(durations, 50));
        Duration p95 = Duration.ofMillis(percentile(durations, 95));
        Duration p99 = Duration.ofMillis(percentile(durations, 99));

        SkillStatistics stats = new SkillStatistics(
                executions, successes, failures, 0, 0,
                avg, p50, p95, p99, 0, 0
        );

        return CompletableFuture.completedFuture(stats);
    }

    @Override
    public CompletionStage<List<SkillInsight>> insights(SkillAnalyticsQuery query) {
        return statistics(query).thenApply(stats -> {
            List<SkillInsight> insights = new ArrayList<>();
            String skillId = query.skillId().orElse("global");

            if (stats.executions() > 0 && stats.failureRate() > 0.3) {
                insights.add(new SkillInsight(
                        "HIGH_FAILURE_RATE",
                        skillId,
                        query.version(),
                        "WARNING",
                        "High failure rate detected",
                        String.format("Failure rate is %.1f%% across %d executions", stats.failureRate() * 100, stats.executions()),
                        Map.of("failureRate", stats.failureRate(), "executions", stats.executions()),
                        java.time.Instant.now()
                ));
            }

            if (stats.executions() > 0 && stats.p95Duration().toMillis() > 5000) {
                insights.add(new SkillInsight(
                        "HIGH_LATENCY",
                        skillId,
                        query.version(),
                        "INFO",
                        "Elevated p95 latency",
                        String.format("p95 execution latency is %d ms", stats.p95Duration().toMillis()),
                        Map.of("p95Millis", stats.p95Duration().toMillis()),
                        java.time.Instant.now()
                ));
            }

            return insights;
        });
    }

    private List<InMemorySkillObservationRecorder.ExecutionRecord> filterRecords(SkillAnalyticsQuery query) {
        return recorder.completedRecords().stream()
                .filter(r -> query.skillId().isEmpty() || query.skillId().get().equals(r.context().skillId()))
                .filter(r -> query.version().isEmpty() || query.version().get().equals(r.context().skillVersion()))
                .collect(Collectors.toList());
    }

    private static long percentile(List<Long> sorted, double pct) {
        if (sorted.isEmpty()) return 0;
        int idx = (int) Math.ceil(pct / 100.0 * sorted.size()) - 1;
        return sorted.get(Math.max(0, Math.min(idx, sorted.size() - 1)));
    }
}
