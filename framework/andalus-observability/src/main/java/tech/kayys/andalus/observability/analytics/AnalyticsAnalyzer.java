package tech.kayys.andalus.observability.analytics;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * SPI for modular analytics analyzers that produce evidence-backed insights.
 */
public interface AnalyticsAnalyzer {

    String id();

    CompletionStage<List<AnalyticsInsight>> analyze(AnalyticsQuery query);
}
