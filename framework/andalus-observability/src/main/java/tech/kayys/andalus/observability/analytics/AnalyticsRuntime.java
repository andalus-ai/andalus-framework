package tech.kayys.andalus.observability.analytics;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Service contract for computing operational analytics, aggregates, and insights.
 */
public interface AnalyticsRuntime {

    CompletionStage<AnalyticsReport> analyze(AnalyticsQuery query);

    CompletionStage<List<AnalyticsInsight>> insights(AnalyticsQuery query);
}
