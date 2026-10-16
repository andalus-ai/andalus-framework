package tech.kayys.andalus.harness.decision.analytics;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Consolidated decision analytics report returned by {@link DecisionAnalyticsAnalyzer} (Phase 4O).
 *
 * <p>Includes lifecycle aggregates, governance outcome counts, resource/cost summaries, and a
 * list of evidence-backed findings. The {@code dataCompleteness} field explicitly indicates when
 * some telemetry was unavailable so that consumers are not misled by incomplete numbers.
 */
public record DecisionAnalyticsReport(
        DecisionAnalyticsQuery query,
        long decisionsStarted,
        long decisionsCompleted,
        long decisionsFailed,
        long decisionsCancelled,
        long decisionsTimedOut,
        long governanceDenials,
        long governanceWaiting,
        long replanningRequests,
        Map<String, Long> outcomesByGovernanceResult,
        Optional<Double> medianDurationMs,
        List<DecisionAnalyticsFinding> findings,
        Map<String, Object> diagnostics,
        boolean dataComplete
) {

    public DecisionAnalyticsReport {
        Objects.requireNonNull(query, "query must not be null");
        outcomesByGovernanceResult = outcomesByGovernanceResult == null
                ? Map.of() : Map.copyOf(outcomesByGovernanceResult);
        medianDurationMs = medianDurationMs == null ? Optional.empty() : medianDurationMs;
        findings = findings == null ? List.of() : List.copyOf(findings);
        diagnostics = diagnostics == null ? Map.of() : Map.copyOf(diagnostics);
    }

    /** Returns an empty report for a query that matched no records. */
    public static DecisionAnalyticsReport empty(DecisionAnalyticsQuery query) {
        return new DecisionAnalyticsReport(
                query, 0, 0, 0, 0, 0, 0, 0, 0,
                Map.of(), Optional.empty(),
                List.of(), Map.of("reason", "no records matched"), true);
    }

    /** Returns an incomplete report when telemetry data was partially unavailable. */
    public static DecisionAnalyticsReport incomplete(
            DecisionAnalyticsQuery query, Map<String, Object> diagnostics) {
        return new DecisionAnalyticsReport(
                query, 0, 0, 0, 0, 0, 0, 0, 0,
                Map.of(), Optional.empty(),
                List.of(), diagnostics, false);
    }
}
