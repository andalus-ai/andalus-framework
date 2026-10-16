package tech.kayys.andalus.harness.decision.analytics;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * SPI for computing decision analytics from recorded observability data (Phase 4O).
 *
 * <p>Implementations aggregate data from the existing {@code EventJournal}, {@code DecisionTrace}
 * records, and {@code MetricsRuntime} without introducing a second analytics engine or event store.
 *
 * <p><b>Architecture invariants</b>
 * <ul>
 *   <li>Analytics findings must not automatically mutate policies, memory, or agent configuration.</li>
 *   <li>Authorization must be checked before data access, not by filtering results afterwards.</li>
 *   <li>Estimated costs and provider-reported charges remain separate fields.</li>
 *   <li>Missing telemetry is surfaced as {@code dataComplete=false}, never silently treated as zero.</li>
 * </ul>
 */
public interface DecisionAnalyticsAnalyzer {

    /**
     * Computes an aggregate decision analytics report for the given query scope.
     *
     * @param query scoped, authorized query; must have been authorization-checked by the caller
     * @return future report (never null; may be {@link DecisionAnalyticsReport#empty} or
     *         {@link DecisionAnalyticsReport#incomplete} when data is unavailable)
     */
    CompletionStage<DecisionAnalyticsReport> analyze(DecisionAnalyticsQuery query);

    /**
     * Returns a list of evidence-backed findings for the given query scope.
     * Returns an empty list if no findings can be generated from available data.
     */
    CompletionStage<List<DecisionAnalyticsFinding>> findings(DecisionAnalyticsQuery query);
}
