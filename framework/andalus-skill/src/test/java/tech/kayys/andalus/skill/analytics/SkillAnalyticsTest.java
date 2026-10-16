package tech.kayys.andalus.skill.analytics;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.skill.observability.InMemorySkillObservationRecorder;
import tech.kayys.andalus.skill.observability.SkillObservationContext;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SkillAnalyticsTest {

    @Test
    void inMemoryAnalyticsComputesCorrectStatistics() throws Exception {
        InMemorySkillObservationRecorder recorder = new InMemorySkillObservationRecorder();

        // Record 3 executions: 2 success, 1 failure
        SkillObservationContext ctx1 = new SkillObservationContext("inv-1", "e-1", "t-1", "u-1", "skill-calc", "1.0", Instant.now());
        recorder.executionStarted(ctx1);
        Thread.sleep(10);
        recorder.executionCompleted(ctx1, SkillResult.success(Map.of()));

        SkillObservationContext ctx2 = new SkillObservationContext("inv-2", "e-2", "t-2", "u-2", "skill-calc", "1.0", Instant.now());
        recorder.executionStarted(ctx2);
        Thread.sleep(15);
        recorder.executionCompleted(ctx2, SkillResult.success(Map.of()));

        SkillObservationContext ctx3 = new SkillObservationContext("inv-3", "e-3", "t-3", "u-3", "skill-calc", "1.0", Instant.now());
        recorder.executionStarted(ctx3);
        Thread.sleep(20);
        recorder.executionFailed(ctx3, new RuntimeException("fail"));

        InMemorySkillAnalytics analytics = new InMemorySkillAnalytics(recorder);

        SkillAnalyticsQuery query = SkillAnalyticsQuery.forSkill("skill-calc");
        SkillStatistics stats = analytics.statistics(query).toCompletableFuture().get();

        assertThat(stats.executions()).isEqualTo(3);
        assertThat(stats.successes()).isEqualTo(2);
        assertThat(stats.failures()).isEqualTo(1);
        assertThat(stats.successRate()).isGreaterThan(0.6).isLessThan(0.7);
        assertThat(stats.failureRate()).isGreaterThan(0.3).isLessThan(0.4);
        assertThat(stats.averageDuration().toMillis()).isGreaterThan(0);
        assertThat(stats.p50Duration().toMillis()).isGreaterThan(0);
    }

    @Test
    void inMemoryAnalyticsGeneratesInsightsOnHighFailureRate() throws Exception {
        InMemorySkillObservationRecorder recorder = new InMemorySkillObservationRecorder();

        // Record 4 failures out of 5 executions (80% failure rate)
        for (int i = 0; i < 5; i++) {
            SkillObservationContext ctx = new SkillObservationContext("inv-" + i, "e-1", "t-1", "u-1", "flaky-skill", "1.0", Instant.now());
            recorder.executionStarted(ctx);
            if (i == 0) {
                recorder.executionCompleted(ctx, SkillResult.success(Map.of()));
            } else {
                recorder.executionFailed(ctx, new RuntimeException("downstream error"));
            }
        }

        InMemorySkillAnalytics analytics = new InMemorySkillAnalytics(recorder);
        List<SkillInsight> insights = analytics.insights(SkillAnalyticsQuery.forSkill("flaky-skill")).toCompletableFuture().get();

        assertThat(insights).isNotEmpty();
        assertThat(insights).anyMatch(ins -> ins.type().equals("HIGH_FAILURE_RATE") && ins.severity().equals("WARNING"));
    }

    @Test
    void emptyAnalyticsReturnsZeroStats() throws Exception {
        InMemorySkillObservationRecorder recorder = new InMemorySkillObservationRecorder();
        InMemorySkillAnalytics analytics = new InMemorySkillAnalytics(recorder);

        SkillStatistics stats = analytics.statistics(SkillAnalyticsQuery.all()).toCompletableFuture().get();
        assertThat(stats.executions()).isEqualTo(0);
        assertThat(stats.successRate()).isEqualTo(1.0);
        assertThat(stats.failureRate()).isEqualTo(0.0);
    }
}
