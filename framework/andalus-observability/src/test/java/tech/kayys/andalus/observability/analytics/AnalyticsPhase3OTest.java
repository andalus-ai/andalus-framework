package tech.kayys.andalus.observability.analytics;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsPhase3OTest {

    @Test
    void testAnalyticsQueryAndRecord() throws Exception {
        AnalyticsStore store = new InMemoryAnalyticsStore();
        AnalyticsRecord record = new AnalyticsRecord("knowledge.resolution.count", Instant.now(), 42.0, Map.of("source", "db-1"));

        store.write(record).toCompletableFuture().get(5, TimeUnit.SECONDS);

        List<AnalyticsRecord> results = store.query(AnalyticsQuery.all()).toCompletableFuture().get(5, TimeUnit.SECONDS);
        assertEquals(1, results.size());
        assertEquals("knowledge.resolution.count", results.get(0).metric());
        assertEquals(42.0, results.get(0).value());
    }

    @Test
    void testDefaultAnalyticsRuntimeWithAnalyzer() throws Exception {
        DefaultAnalyticsRuntime runtime = new DefaultAnalyticsRuntime(new InMemoryAnalyticsStore());

        AnalyticsAnalyzer mockAnalyzer = new AnalyticsAnalyzer() {
            @Override
            public String id() {
                return "knowledge-reliability-analyzer";
            }

            @Override
            public java.util.concurrent.CompletionStage<List<AnalyticsInsight>> analyze(AnalyticsQuery query) {
                AnalyticsEvidence evidence = AnalyticsEvidence.ofMetric("source-1", "knowledge.source.failure");
                AnalyticsInsight insight = AnalyticsInsight.warning(
                        "KNOWLEDGE_RELIABILITY",
                        "Source failure rate high",
                        "Failure rate exceeded threshold",
                        List.of(evidence)
                );
                return CompletableFuture.completedFuture(List.of(insight));
            }
        };

        runtime.registerAnalyzer(mockAnalyzer);

        AnalyticsReport report = runtime.analyze(AnalyticsQuery.all()).toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertNotNull(report);
        assertEquals(1, report.insights().size());
        AnalyticsInsight insight = report.insights().get(0);
        assertEquals("KNOWLEDGE_RELIABILITY", insight.category());
        assertEquals(AnalyticsSeverity.WARNING, insight.severity());
        assertEquals(1, insight.evidence().size());
        assertEquals("source-1", insight.evidence().get(0).sourceId());
    }

    @Test
    void testKnowledgeAndMemoryStatisticsContracts() {
        KnowledgeSourceStatistics sourceStats = new KnowledgeSourceStatistics(
                "kb-1", 100, 95, 5, 0, 300, Duration.ofMillis(50), Duration.ofMillis(120), 0.05
        );
        KnowledgeStatistics kbStats = new KnowledgeStatistics(
                100, 95, 5, 10, 150, 5, 0, 400, 350, 50, 2,
                Duration.ofMillis(80), Duration.ofMillis(150),
                Map.of("kb-1", sourceStats), Map.of()
        );

        assertEquals(100, kbStats.resolutionCount());
        assertEquals(95, kbStats.sources().get("kb-1").successCount());

        MemoryStatistics memStats = new MemoryStatistics(
                50, 45, 5, 0, 30, 28, 2, 28, 0, 0, 0, 1, 1,
                Duration.ofMillis(15), Duration.ofMillis(30),
                Map.of("duplicate", 2L), Map.of()
        );

        assertEquals(50, memStats.retrievalCount());
        assertEquals(2L, memStats.rejectionReasons().get("duplicate"));
    }
}
