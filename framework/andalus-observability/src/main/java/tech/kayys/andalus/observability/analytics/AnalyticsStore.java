package tech.kayys.andalus.observability.analytics;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Storage contract for persisting and retrieving analytical records.
 */
public interface AnalyticsStore {

    CompletionStage<Void> write(AnalyticsRecord record);

    CompletionStage<List<AnalyticsRecord>> query(AnalyticsQuery query);
}
