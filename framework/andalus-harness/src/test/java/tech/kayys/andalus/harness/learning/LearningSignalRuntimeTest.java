package tech.kayys.andalus.harness.learning;

import org.junit.jupiter.api.BeforeEach;
import tech.kayys.andalus.harness.evaluation.Evaluator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LearningSignalRuntimeTest {

    private DefaultLearningSignalRuntime runtime;
    private final String executionId = "exec-learn-1";

    @BeforeEach
    void setUp() {
        runtime = new DefaultLearningSignalRuntime();
    }

    @Test
    void validEvaluationSignal_isAccepted() {
        LearningSignal signal = LearningSignal.ofEvaluation(
                "acceptance-test-suite",
                executionId,
                "criterion-42",
                LearningSignalPolarity.POSITIVE,
                List.of("obs-build-pass")
        );

        LearningSignalIngestionResult result = runtime.ingest(signal).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.status()).isEqualTo(LearningSignalIngestionStatus.ACCEPTED);
        assertThat(result.signalId()).isEqualTo(signal.id());
    }

    @Test
    void duplicateSignalById_returnsDuplicateStatus() {
        LearningSignal signal = LearningSignal.of(
                LearningSignalType.OUTCOME,
                "tool-exec",
                executionId,
                LearningSignalPolarity.POSITIVE,
                List.of()
        );

        LearningSignalIngestionResult first = runtime.ingest(signal).toCompletableFuture().join();
        assertThat(first.status()).isEqualTo(LearningSignalIngestionStatus.ACCEPTED);

        LearningSignalIngestionResult second = runtime.ingest(signal).toCompletableFuture().join();
        assertThat(second.status()).isEqualTo(LearningSignalIngestionStatus.DUPLICATE);
    }

    @Test
    void duplicateSignalByFingerprint_returnsDuplicateStatus() {
        LearningSignal signalA = new LearningSignal(
                "sig-A",
                LearningSignalType.TOOL_RESULT,
                "fs-tool",
                executionId,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                LearningSignalPolarity.POSITIVE,
                List.of(),
                Optional.empty(),
                null,
                Map.of()
        );

        LearningSignal signalB = new LearningSignal(
                "sig-B", // different ID
                LearningSignalType.TOOL_RESULT,
                "fs-tool", // same source, execution, type
                executionId,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                LearningSignalPolarity.POSITIVE,
                List.of(),
                Optional.empty(),
                null,
                Map.of()
        );

        assertThat(runtime.ingest(signalA).toCompletableFuture().join().status())
                .isEqualTo(LearningSignalIngestionStatus.ACCEPTED);

        assertThat(runtime.ingest(signalB).toCompletableFuture().join().status())
                .isEqualTo(LearningSignalIngestionStatus.DUPLICATE);
    }

    @Test
    void missingExecutionId_throwsException() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> new LearningSignal(
                "sig-invalid",
                LearningSignalType.OUTCOME,
                "src",
                "", // empty executionId
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                LearningSignalPolarity.NEUTRAL,
                List.of(),
                Optional.empty(),
                null,
                Map.of()
        )).isInstanceOf(IllegalArgumentException.class);

        LearningSignalIngestionResult result = runtime.ingest(null).toCompletableFuture().join();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.status()).isEqualTo(LearningSignalIngestionStatus.REJECTED);
    }

    @Test
    void query_filtersByExecutionIdAndPolarity() {
        runtime.ingest(LearningSignal.of(LearningSignalType.OUTCOME, "src1", "exec-A", LearningSignalPolarity.POSITIVE, List.of()));
        runtime.ingest(LearningSignal.of(LearningSignalType.OUTCOME, "src2", "exec-A", LearningSignalPolarity.NEGATIVE, List.of()));
        runtime.ingest(LearningSignal.of(LearningSignalType.OUTCOME, "src3", "exec-B", LearningSignalPolarity.POSITIVE, List.of()));

        LearningSignalQuery query = new LearningSignalQuery(
                Optional.of("exec-A"),
                Optional.of(LearningSignalType.OUTCOME),
                Optional.of(LearningSignalPolarity.POSITIVE),
                10
        );

        LearningSignalQueryResult result = runtime.query(query).toCompletableFuture().join();

        assertThat(result.totalCount()).isEqualTo(1);
        assertThat(result.signals().get(0).executionId()).isEqualTo("exec-A");
        assertThat(result.signals().get(0).polarity()).isEqualTo(LearningSignalPolarity.POSITIVE);
    }

    @Test
    void humanFeedbackSignal_preservesFeedbackContext() {
        LearningSignal feedbackSignal = new LearningSignal(
                "feedback-1",
                LearningSignalType.HUMAN_FEEDBACK,
                "reviewer-alice",
                executionId,
                Optional.of("turn-3"),
                Optional.empty(),
                Optional.empty(),
                LearningSignalPolarity.MIXED,
                List.of("obs-report-v1"),
                Optional.empty(),
                null,
                Map.of("correction", "Corrected inflation rate to 3.2%")
        );

        LearningSignalIngestionResult res = runtime.ingest(feedbackSignal).toCompletableFuture().join();
        assertThat(res.isSuccess()).isTrue();

        LearningSignalQueryResult queryRes = runtime.query(LearningSignalQuery.forType(LearningSignalType.HUMAN_FEEDBACK)).toCompletableFuture().join();
        assertThat(queryRes.totalCount()).isEqualTo(1);
        assertThat(queryRes.signals().get(0).metadata()).containsKey("correction");
    }

    @Test
    void queryLimit_isRespected() {
        for (int i = 0; i < 10; i++) {
            runtime.ingest(LearningSignal.of(
                    LearningSignalType.OUTCOME,
                    "batch-" + i,
                    executionId,
                    LearningSignalPolarity.NEUTRAL,
                    List.of()
            ));
        }

        LearningSignalQuery query = new LearningSignalQuery(
                Optional.of(executionId),
                Optional.empty(),
                Optional.empty(),
                4 // limit 4
        );

        LearningSignalQueryResult result = runtime.query(query).toCompletableFuture().join();
        assertThat(result.totalCount()).isEqualTo(4);
    }
    @Test
    void evaluationRuntime_aggregatesMetricsAndPreservesProvenance() {
        Evaluator evaluator = item -> java.util.concurrent.CompletableFuture.completedFuture(
                new Evaluator.CaseResult(item.id(), Map.of("quality", item.id().equals("case-1") ? 0.8 : 1.0,
                        "latency_ms", item.id().equals("case-1") ? 100.0 : 200.0), true, "ok"));
        Evaluator.Dataset dataset = new Evaluator.Dataset("smoke", "v1", List.of(
                new Evaluator.EvaluationCase("case-1", "input-1", "expected-1", Map.of()),
                new Evaluator.EvaluationCase("case-2", "input-2", "expected-2", Map.of())));
        Evaluator.Request request = new Evaluator.Request("eval-1", "model-a", "v2", dataset);
        Evaluator.Report report = new Evaluator.DefaultRuntime(evaluator).evaluate(request).toCompletableFuture().join();

        assertThat(report.candidateVersion()).isEqualTo("v2");
        assertThat(report.datasetVersion()).isEqualTo("v1");
        assertThat(report.passRate()).isEqualTo(1.0);
        assertThat(report.aggregateMetrics()).containsEntry("quality", 0.9).containsEntry("latency_ms", 150.0);
    }

    @Test
    void promotionGate_rejectsMissingOrOutOfBoundsMetrics() {
        Evaluator evaluator = item -> java.util.concurrent.CompletableFuture.completedFuture(
                new Evaluator.CaseResult(item.id(), Map.of("quality", 0.6), false, "below target"));
        Evaluator.Dataset dataset = new Evaluator.Dataset("smoke", "v1",
                List.of(new Evaluator.EvaluationCase("case-1", "input", "expected", Map.of())));
        Evaluator.Report report = new Evaluator.DefaultRuntime(evaluator).evaluate(
                new Evaluator.Request("eval-2", "model-a", "v3", dataset)).toCompletableFuture().join();
        Evaluator.PromotionGate gate = new Evaluator.PromotionGate(0.9, List.of(
                new Evaluator.MetricThreshold("quality", Evaluator.Direction.AT_LEAST, 0.8),
                new Evaluator.MetricThreshold("safety", Evaluator.Direction.AT_LEAST, 1.0)));
        Evaluator.PromotionDecision decision = gate.assess(report);

        assertThat(decision.approved()).isFalse();
        assertThat(decision.reasons()).hasSize(3);
    }

    @Test
    void evaluationRuntime_rejectsMismatchedCaseResultIdentity() {
        Evaluator evaluator = item -> java.util.concurrent.CompletableFuture.completedFuture(
                new Evaluator.CaseResult("wrong-id", Map.of(), true, null));
        Evaluator.Dataset dataset = new Evaluator.Dataset("smoke", "v1",
                List.of(new Evaluator.EvaluationCase("case-1", "input", "expected", Map.of())));
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                new Evaluator.DefaultRuntime(evaluator).evaluate(
                        new Evaluator.Request("eval-3", "model-a", "v1", dataset)).toCompletableFuture().join())
                .isInstanceOf(java.util.concurrent.CompletionException.class);
    }


    @Test
    void reportStore_persistsAndRetrievesEvaluationProvenance() {
        Evaluator.Report report = sampleReport("eval-store", "v1", 1.0, Map.of("quality", 0.9));
        Evaluator.InMemoryReportStore store = new Evaluator.InMemoryReportStore();
        store.save(report).toCompletableFuture().join();

        assertThat(store.findByEvaluationId("eval-store").toCompletableFuture().join()).contains(report);
        assertThat(store.findByEvaluationId("missing").toCompletableFuture().join()).isEmpty();
    }

    @Test
    void regressionAnalyzer_detectsMetricAndPassRateRegression() {
        Evaluator.Report baseline = sampleReport("baseline", "v1", 1.0, Map.of("quality", 0.9));
        Evaluator.Report candidate = sampleReport("candidate", "v1", 0.5, Map.of("quality", 0.7));
        Evaluator.RegressionAnalyzer analyzer = new Evaluator.RegressionAnalyzer(0.1, List.of(
                new Evaluator.RegressionThreshold("quality", 0.05)));

        Evaluator.Comparison comparison = analyzer.compare(baseline, candidate);

        assertThat(comparison.regressed()).isTrue();
        assertThat(comparison.passRateDelta()).isEqualTo(-0.5);
        assertThat(comparison.metricDeltas()).containsEntry("quality", -0.2);
        assertThat(comparison.reasons()).hasSize(2);
    }

    @Test
    void regressionAnalyzer_requiresIdenticalDatasetRevision() {
        Evaluator.Report baseline = sampleReport("baseline", "v1", 1.0, Map.of());
        Evaluator.Report candidate = sampleReport("candidate", "v2", 1.0, Map.of());

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                new Evaluator.RegressionAnalyzer(0.0, List.of()).compare(baseline, candidate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Evaluator.Report sampleReport(String evaluationId, String datasetVersion,
                                          double passRate, Map<String,Double> metrics) {
        List<Evaluator.CaseResult> results = List.of(
                new Evaluator.CaseResult("case-1", metrics, passRate >= 0.5, null),
                new Evaluator.CaseResult("case-2", metrics, passRate >= 1.0, null));
        return new Evaluator.Report(evaluationId, "model-a", "candidate-v1", "regression-suite",
                datasetVersion, java.time.Instant.parse("2026-01-01T00:00:00Z"),
                java.time.Instant.parse("2026-01-01T00:00:01Z"), results, metrics);
    }

    @Test
    void evaluatorDescriptor_fingerprintIsStableAcrossMapOrder() {
        Evaluator.EvaluatorDescriptor first = new Evaluator.EvaluatorDescriptor("judge", "2.1",
                new java.util.LinkedHashMap<>(Map.of("temperature", "0", "rubric", "strict")));
        java.util.Map<String,String> reverse = new java.util.LinkedHashMap<>();
        reverse.put("rubric", "strict");
        reverse.put("temperature", "0");
        Evaluator.EvaluatorDescriptor second = new Evaluator.EvaluatorDescriptor("judge", "2.1", reverse);

        assertThat(first.configurationFingerprint()).isEqualTo(second.configurationFingerprint());
        assertThat(first.configurationFingerprint()).hasSize(64);
    }

    @Test
    void evaluationReport_recordsEvaluatorIdentityAndConfigurationFingerprint() {
        Evaluator evaluator = item -> java.util.concurrent.CompletableFuture.completedFuture(
                new Evaluator.CaseResult(item.id(), Map.of("quality", 1.0), true, null));
        Evaluator.Dataset dataset = new Evaluator.Dataset("repro", "v4",
                List.of(new Evaluator.EvaluationCase("case-1", "input", "expected", Map.of())));
        Evaluator.EvaluatorDescriptor descriptor = new Evaluator.EvaluatorDescriptor("llm-judge", "3.2",
                Map.of("temperature", "0", "rubric", "safety-v2"));
        Evaluator.Report report = new Evaluator.DefaultRuntime(evaluator).evaluate(
                new Evaluator.Request("eval-repro", "model-x", "sha-123", dataset, descriptor))
                .toCompletableFuture().join();

        assertThat(report.evaluatorId()).isEqualTo("llm-judge");
        assertThat(report.evaluatorVersion()).isEqualTo("3.2");
        assertThat(report.evaluatorConfigFingerprint()).isEqualTo(descriptor.configurationFingerprint());
    }
}

    @Test
    void evaluationAdapter_preservesProvenanceAndClassifiesCompletedEvaluation() {
        Evaluator.Report report = sampleReport("eval-adapter", "v7", 0.5, Map.of("quality", 0.82));
        Evaluator.Report attributed = new Evaluator.Report(report.evaluationId(), report.candidateId(),
                report.candidateVersion(), report.datasetId(), report.datasetVersion(), report.startedAt(),
                report.completedAt(), List.of(
                        new Evaluator.CaseResult("case-1", Map.of("quality", 0.82), true, "private detail"),
                        new Evaluator.CaseResult("case-2", Map.of("quality", 0.82), false, "private detail")),
                report.aggregateMetrics(), "judge-v2", "4.1", "abc123");

        LearningSignal signal = EvaluationLearningSignalAdapter.adapt(attributed,
                new EvaluationLearningSignalAdapter.Policy(2, 2.0, 0.5)).orElseThrow();

        assertThat(signal.type()).isEqualTo(LearningSignalType.EVALUATION);
        assertThat(signal.polarity()).isEqualTo(LearningSignalPolarity.MIXED);
        assertThat(signal.executionId()).isEqualTo("eval-adapter");
        assertThat(signal.evaluationReference()).contains("eval-adapter");
        assertThat(signal.metadata())
                .containsEntry("candidateId", "model-a")
                .containsEntry("candidateVersion", "candidate-v1")
                .containsEntry("datasetVersion", "v7")
                .containsEntry("evaluatorId", "judge-v2")
                .containsEntry("evaluatorVersion", "4.1")
                .containsEntry("evaluatorConfigFingerprint", "abc123")
                .containsEntry("caseCount", 2)
                .containsEntry("confidence", 0.5);
        assertThat(signal.metadata()).doesNotContainKey("input").doesNotContainKey("expectedOutput");
        assertThat(signal.metadata().toString()).doesNotContain("private detail");
    }

    @Test
    void evaluationAdapter_requiresMinimumEvidenceBeforeEmittingSignal() {
        Evaluator.Report report = sampleReport("eval-small", "v1", 1.0, Map.of("quality", 1.0));

        assertThat(EvaluationLearningSignalAdapter.adapt(report,
                new EvaluationLearningSignalAdapter.Policy(3, 2.0, 0.5))).isEmpty();
        assertThat(EvaluationLearningSignalAdapter.adapt(report,
                new EvaluationLearningSignalAdapter.Policy(2, 10.0, 0.5))).isEmpty();
    }

    @Test
    void evaluationAdapter_doesNotConflateHumanFeedbackWithAutomatedEvaluation() {
        Evaluator.Report report = sampleReport("eval-human-distinct", "v1", 1.0, Map.of("quality", 1.0));
        LearningSignal automated = EvaluationLearningSignalAdapter.adapt(report,
                new EvaluationLearningSignalAdapter.Policy(2, 1.0, 0.5)).orElseThrow();
        LearningSignal human = new LearningSignal("human-1", LearningSignalType.HUMAN_FEEDBACK,
                "reviewer", "exec-human", Optional.empty(), Optional.empty(), Optional.empty(),
                LearningSignalPolarity.NEGATIVE, List.of("review:1"), Optional.empty(), null,
                Map.of("reason", "reviewer correction"));

        assertThat(automated.type()).isEqualTo(LearningSignalType.EVALUATION);
        assertThat(human.type()).isEqualTo(LearningSignalType.HUMAN_FEEDBACK);
        assertThat(automated.source()).startsWith("evaluation:");
    }

}
