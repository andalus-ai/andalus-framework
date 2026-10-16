package tech.kayys.andalus.harness.reflection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionRuntimeTest {

    private DefaultReflectionRuntime runtime;
    private final String executionId = "exec-reflect-1";

    @BeforeEach
    void setUp() {
        runtime = new DefaultReflectionRuntime(3, 10);
    }

    @Test
    void actionFailure_withObservations_producesFailureFindingAndRetryProposal() {
        ReflectionRequest req = ReflectionRequest.of(
                executionId,
                "turn-1",
                ReflectionTrigger.ACTION_FAILURE,
                List.of("obs-fail-1")
        );

        ReflectionResult result = runtime.reflect(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.status()).isEqualTo(ReflectionStatus.COMPLETED);
        assertThat(result.findings()).hasSize(1);
        assertThat(result.findings().get(0).type()).isEqualTo(ReflectionFindingType.TOOL_FAILURE_PATTERN);
        assertThat(result.proposals()).hasSize(1);
        assertThat(result.proposals().get(0).type()).isEqualTo(ReflectionProposalType.RETRY_WITH_CHANGED_APPROACH);
    }

    @Test
    void actionFailure_withoutObservations_returnsInsufficientEvidence() {
        ReflectionRequest req = ReflectionRequest.of(
                executionId,
                "turn-1",
                ReflectionTrigger.ACTION_FAILURE,
                List.of()
        );

        ReflectionResult result = runtime.reflect(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.status()).isEqualTo(ReflectionStatus.INSUFFICIENT_EVIDENCE);
    }

    @Test
    void taskCompletion_producesOutcomeConfirmedFinding() {
        ReflectionRequest req = ReflectionRequest.of(
                executionId,
                "turn-2",
                ReflectionTrigger.TASK_COMPLETION,
                List.of("obs-success-1")
        );

        ReflectionResult result = runtime.reflect(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.findings().get(0).type()).isEqualTo(ReflectionFindingType.OUTCOME_CONFIRMED);
        assertThat(result.proposals().get(0).type()).isEqualTo(ReflectionProposalType.NO_CHANGE);
    }

    @Test
    void contradictingEvidence_producesReassessBeliefProposal() {
        ReflectionRequest req = ReflectionRequest.of(
                executionId,
                "turn-3",
                ReflectionTrigger.CONTRADICTING_EVIDENCE,
                List.of("obs-diff-1")
        );

        ReflectionResult result = runtime.reflect(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.findings().get(0).type()).isEqualTo(ReflectionFindingType.CONTRADICTING_EVIDENCE);
        assertThat(result.proposals().get(0).type()).isEqualTo(ReflectionProposalType.REASSESS_BELIEF);
    }

    @Test
    void humanCorrection_producesMemoryPromotionCandidate() {
        ReflectionRequest req = ReflectionRequest.of(
                executionId,
                "turn-4",
                ReflectionTrigger.HUMAN_CORRECTION,
                List.of("obs-human-feedback")
        );

        ReflectionResult result = runtime.reflect(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.findings().get(0).type()).isEqualTo(ReflectionFindingType.POTENTIAL_IMPROVEMENT);
        assertThat(result.proposals().get(0).type()).isEqualTo(ReflectionProposalType.PROMOTE_TO_MEMORY_CANDIDATE);
    }

    @Test
    void planDeviation_producesRevisePlanProposal() {
        ReflectionRequest req = ReflectionRequest.of(
                executionId,
                "turn-5",
                ReflectionTrigger.PLAN_DEVIATION,
                List.of("obs-deviation")
        );

        ReflectionResult result = runtime.reflect(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.findings().get(0).type()).isEqualTo(ReflectionFindingType.PLAN_DEVIATION);
        assertThat(result.proposals().get(0).type()).isEqualTo(ReflectionProposalType.REVISE_PLAN);
    }

    @Test
    void recursiveReflectionLoop_isPreventedByExecutionLimit() {
        // maxReflectionsPerExecution is 3
        ReflectionRequest req = ReflectionRequest.of(executionId, "t1", ReflectionTrigger.TASK_COMPLETION);

        assertThat(runtime.reflect(req).toCompletableFuture().join().status()).isEqualTo(ReflectionStatus.COMPLETED);
        assertThat(runtime.reflect(req).toCompletableFuture().join().status()).isEqualTo(ReflectionStatus.COMPLETED);
        assertThat(runtime.reflect(req).toCompletableFuture().join().status()).isEqualTo(ReflectionStatus.COMPLETED);

        // 4th call exceeds limit of 3
        ReflectionResult fourth = runtime.reflect(req).toCompletableFuture().join();
        assertThat(fourth.status()).isEqualTo(ReflectionStatus.SKIPPED);
    }

    @Test
    void nullRequest_returnsFailedStatus() {
        ReflectionResult result = runtime.reflect(null).toCompletableFuture().join();
        assertThat(result.status()).isEqualTo(ReflectionStatus.FAILED);
    }
}
