package tech.kayys.andalus.skill.analytics;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Service contract for computing operational statistics and deriving insights
 * from skill executions.
 */
public interface SkillAnalytics {

    /**
     * Compute statistics for the given query filter.
     */
    CompletionStage<SkillStatistics> statistics(SkillAnalyticsQuery query);

    /**
     * Compute derived insights for the given query filter.
     */
    CompletionStage<List<SkillInsight>> insights(SkillAnalyticsQuery query);
}
