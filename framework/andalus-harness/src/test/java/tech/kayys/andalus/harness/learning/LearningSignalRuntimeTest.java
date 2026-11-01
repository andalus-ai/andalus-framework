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

}
