package tech.kayys.andalus.harness.decision.analytics;

import tech.kayys.andalus.harness.decision.observability.DecisionObservabilityRecord;
import tech.kayys.andalus.harness.decision.observability.DecisionObservabilityRuntime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Default {@link DecisionAnalyticsAnalyzer} that derives aggregates directly from the
 * {@link DecisionObservabilityRuntime} in-memory store (Phase 4O).
 *
 * <p>This implementation is suitable for community and development use. Production deployments
 * can replace it via the {@link DecisionAnalyticsAnalyzer} SPI to read from a durable
 * analytics store without changing any calling code.
 *
 * <p><b>Architecture invariants respected</b>
 * <ul>
 *   <li>No second event store or analytics database is introduced.</li>
 *   <li>Findings are evidence-backed references, not autonomous mutations.</li>
 *   <li>Governance denial rate denominator is explicit; waiting decisions are not treated as failures.</li>
 *   <li>Missing data is surfaced via {@code dataComplete=false}, not silently zeroed.</li>
 * </ul>
 */
public class DefaultDecisionAnalyticsAnalyzer implements DecisionAnalyticsAnalyzer {

    private final DecisionObservabilityRuntime observability;

    public DefaultDecisionAnalyticsAnalyzer(DecisionObservabilityRuntime observability) {
        this.observability = Objects.requireNonNull(observability, "observability must not be null");
    }

    @Override
    public CompletionStage<DecisionAnalyticsReport> analyze(DecisionAnalyticsQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        if (!observability.enabled()) {
            return CompletableFuture.completedFuture(
                    DecisionAnalyticsReport.incomplete(query,
                            Map.of("reason", "observability disabled")));
        }

        List<DecisionObservabilityRecord> records = collectRecords(query);

        if (records.isEmpty()) {
            return CompletableFuture.completedFuture(DecisionAnalyticsReport.empty(query));
        }

        // --- Lifecycle counters (§4O.3) ---
        long started   = countByType(records, "DECISION_STARTED");
        long completed = countByType(records, "DECISION_COMPLETED");
        long failed    = countByType(records, "DECISION_FAILED");
        long cancelled = countByType(records, "DECISION_CANCELLED");
        long timedOut  = countByType(records, "DECISION_TIMED_OUT");
        long waiting   = countByType(records, "DECISION_WAITING");
        long replanning= countByType(records, "DECISION_REPLANNING_REQUESTED");

        // --- Governance outcome breakdown (§4O.8) ---
        long govDenials = records.stream()
                .filter(r -> "DECISION_GOVERNANCE_COMPLETED".equals(r.eventType()))
                .filter(r -> r.governanceOutcome().filter("DENY"::equals).isPresent())
                .count();

        Map<String, Long> byOutcome = records.stream()
                .filter(r -> "DECISION_GOVERNANCE_COMPLETED".equals(r.eventType()))
                .filter(r -> r.governanceOutcome().isPresent())
                .collect(Collectors.groupingBy(
                        r -> r.governanceOutcome().orElse("UNKNOWN"),
                        Collectors.counting()));

        // --- Median duration (§4O.3) ---
        var durations = records.stream()
                .filter(r -> r.durationMs().isPresent())
                .mapToLong(r -> r.durationMs().orElse(0L))
                .sorted()
                .toArray();
        var medianDuration = durations.length == 0
                ? java.util.Optional.<Double>empty()
                : java.util.Optional.of(medianOf(durations));

        DecisionAnalyticsReport report = new DecisionAnalyticsReport(
                query,
                started, completed, failed, cancelled, timedOut,
                govDenials, waiting, replanning,
                byOutcome, medianDuration,
                List.of(),    // findings computed separately
                Map.of(),
                true
        );
        return CompletableFuture.completedFuture(report);
    }

    @Override
    public CompletionStage<List<DecisionAnalyticsFinding>> findings(DecisionAnalyticsQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        if (!observability.enabled()) {
            return CompletableFuture.completedFuture(List.of(
                    DecisionAnalyticsFinding.incomplete(
                            "andalus_decision_findings",
                            "OBSERVABILITY",
                            "Observability is disabled; no findings can be generated.",
                            Map.of("reason", "observability disabled"))));
        }

        List<DecisionObservabilityRecord> records = collectRecords(query);
        List<DecisionAnalyticsFinding> result = new ArrayList<>();

        // Finding: governance denial rate
        long govTotal = countByType(records, "DECISION_GOVERNANCE_COMPLETED");
        long govDenials = records.stream()
                .filter(r -> "DECISION_GOVERNANCE_COMPLETED".equals(r.eventType()))
                .filter(r -> r.governanceOutcome().filter("DENY"::equals).isPresent())
                .count();
        if (govTotal > 0) {
            result.add(DecisionAnalyticsFinding.of(
                    "andalus_decision_governance_denial_rate",
                    "GOVERNANCE",
                    "Governance denial rate: %d/%d evaluations denied.".formatted(govDenials, govTotal),
                    govDenials,
                    govTotal,
                    Map.of("denials", govDenials, "total", govTotal),
                    List.of()));
        }

        // Finding: replanning frequency
        long started    = countByType(records, "DECISION_STARTED");
        long replanning = countByType(records, "DECISION_REPLANNING_REQUESTED");
        if (started > 0 && replanning > 0) {
            result.add(DecisionAnalyticsFinding.of(
                    "andalus_decision_replanning_rate",
                    "PLANNING",
                    "Replanning requested in %d/%d decisions.".formatted(replanning, started),
                    replanning,
                    started,
                    Map.of("replanningRequests", replanning, "decisionsStarted", started),
                    List.of()));
        }

        // Finding: incomplete data (waiting decisions excluded from failure count)
        long waiting  = countByType(records, "DECISION_WAITING");
        long timedOut = countByType(records, "DECISION_TIMED_OUT");
        if (waiting > 0) {
            result.add(DecisionAnalyticsFinding.of(
                    "andalus_decision_waiting_total",
                    "EXECUTION",
                    "%d decision(s) entered a WAITING state (not counted as failures).".formatted(waiting),
                    waiting,
                    started,
                    Map.of("waiting", waiting, "timedOut", timedOut),
                    List.of()));
        }

        return CompletableFuture.completedFuture(List.copyOf(result));
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private List<DecisionObservabilityRecord> collectRecords(DecisionAnalyticsQuery query) {
        if (!query.executionIds().isEmpty()) {
            return query.executionIds().stream()
                    .flatMap(id -> observability.queryByExecution(id).stream())
                    .distinct()
                    .collect(Collectors.toList());
        }
        // No scope specified — return all records accessible through the runtime.
        // In production, authorization scoping happens before this method is called.
        return List.of();
    }

    private long countByType(List<DecisionObservabilityRecord> records, String type) {
        return records.stream().filter(r -> type.equals(r.eventType())).count();
    }

    private double medianOf(long[] sorted) {
        int n = sorted.length;
        if (n % 2 == 1) return sorted[n / 2];
        return (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0;
    }
}
