package tech.kayys.andalus.harness.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BeliefRuntimeTest {

    private DefaultBeliefRuntime runtime;
    private final String executionId = "exec-101";

    @BeforeEach
    void setUp() {
        runtime = new DefaultBeliefRuntime();
    }

    @Test
    void createBelief_returnsCreatedStatus() {
        BeliefEvidenceReference evidence = BeliefEvidenceReference.ofObservation("obs-1");
        BeliefUpdateRequest request = BeliefUpdateRequest.create(
                executionId,
                "module-auth",
                "Authentication API requires Bearer token",
                List.of(evidence)
        );

        BeliefUpdateResult result = runtime.update(request).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.status()).isEqualTo(BeliefUpdateStatus.CREATED);
        assertThat(result.belief()).isPresent();

        Belief belief = result.belief().get();
        assertThat(belief.subject()).isEqualTo("module-auth");
        assertThat(belief.proposition()).isEqualTo("Authentication API requires Bearer token");
        assertThat(belief.status()).isEqualTo(BeliefStatus.ACTIVE);
        assertThat(belief.revision()).isEqualTo(1L);
        assertThat(belief.evidence()).hasSize(1);
    }

    @Test
    void updateBeliefWithEvidence_incrementsRevision() {
        BeliefUpdateRequest createReq = BeliefUpdateRequest.create(
                executionId,
                "compiler",
                "Compiler target is Java 21",
                List.of(BeliefEvidenceReference.ofObservation("obs-1"))
        );
        Belief created = runtime.update(createReq).toCompletableFuture().join().belief().orElseThrow();

        BeliefEvidenceReference newEvidence = BeliefEvidenceReference.ofObservation("obs-2");
        BeliefUpdateRequest updateReq = BeliefUpdateRequest.update(
                executionId,
                created.id(),
                "compiler",
                "Compiler target is Java 25",
                List.of(newEvidence),
                BeliefUpdateReason.NEW_EVIDENCE,
                1L
        );

        BeliefUpdateResult result = runtime.update(updateReq).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.status()).isEqualTo(BeliefUpdateStatus.UPDATED);
        Belief updated = result.belief().orElseThrow();
        assertThat(updated.proposition()).isEqualTo("Compiler target is Java 25");
        assertThat(updated.revision()).isEqualTo(2L);
        assertThat(updated.evidence()).hasSize(2);
    }

    @Test
    void contradictingEvidence_setsStatusToDisputed() {
        BeliefUpdateRequest createReq = BeliefUpdateRequest.create(
                executionId,
                "rate-limit",
                "Rate limit is 100 req/min",
                List.of(BeliefEvidenceReference.ofObservation("obs-1"))
        );
        Belief created = runtime.update(createReq).toCompletableFuture().join().belief().orElseThrow();

        BeliefUpdateRequest disputeReq = BeliefUpdateRequest.update(
                executionId,
                created.id(),
                "rate-limit",
                "Conflicting headers show rate limit 50 req/min",
                List.of(BeliefEvidenceReference.ofObservation("obs-conflict")),
                BeliefUpdateReason.CONTRADICTING_EVIDENCE,
                created.revision()
        );

        BeliefUpdateResult result = runtime.update(disputeReq).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        Belief disputed = result.belief().orElseThrow();
        assertThat(disputed.status()).isEqualTo(BeliefStatus.DISPUTED);
    }

    @Test
    void explicitRetraction_setsStatusToRetracted() {
        BeliefUpdateRequest createReq = BeliefUpdateRequest.create(
                executionId,
                "cache",
                "Cache is warm",
                List.of()
        );
        Belief created = runtime.update(createReq).toCompletableFuture().join().belief().orElseThrow();

        BeliefUpdateRequest retractReq = BeliefUpdateRequest.retract(
                executionId,
                created.id(),
                created.revision()
        );

        BeliefUpdateResult result = runtime.update(retractReq).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        Belief retracted = result.belief().orElseThrow();
        assertThat(retracted.status()).isEqualTo(BeliefStatus.RETRACTED);
    }

    @Test
    void optimisticConcurrencyConflict_isDetected() {
        BeliefUpdateRequest createReq = BeliefUpdateRequest.create(
                executionId,
                "endpoint",
                "Endpoint is /api/v1",
                List.of()
        );
        Belief created = runtime.update(createReq).toCompletableFuture().join().belief().orElseThrow();

        // Pass expected revision 99 instead of 1
        BeliefUpdateRequest staleUpdate = BeliefUpdateRequest.update(
                executionId,
                created.id(),
                "endpoint",
                "Endpoint is /api/v2",
                List.of(),
                BeliefUpdateReason.NEW_EVIDENCE,
                99L
        );

        BeliefUpdateResult result = runtime.update(staleUpdate).toCompletableFuture().join();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.status()).isEqualTo(BeliefUpdateStatus.CONFLICT);
    }

    @Test
    void nonExistentBelief_returnsNotFound() {
        BeliefUpdateRequest req = BeliefUpdateRequest.update(
                executionId,
                "missing-id",
                "sub",
                "prop",
                List.of(),
                BeliefUpdateReason.NEW_EVIDENCE,
                1L
        );

        BeliefUpdateResult result = runtime.update(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.status()).isEqualTo(BeliefUpdateStatus.NOT_FOUND);
    }

    @Test
    void beliefSnapshot_filtersActiveAndDisputed() {
        runtime.update(BeliefUpdateRequest.create(executionId, "sub1", "Active prop", List.of()));
        Belief toDispute = runtime.update(BeliefUpdateRequest.create(executionId, "sub2", "Disputed prop", List.of())).toCompletableFuture().join().belief().orElseThrow();
        runtime.update(BeliefUpdateRequest.update(executionId, toDispute.id(), "sub2", "Disputed prop", List.of(), BeliefUpdateReason.CONTRADICTING_EVIDENCE, 1L));

        BeliefSnapshot snapshot = runtime.snapshot(executionId).toCompletableFuture().join();

        assertThat(snapshot.beliefs()).hasSize(2);
        assertThat(snapshot.activeBeliefs()).hasSize(1);
        assertThat(snapshot.disputedBeliefs()).hasSize(1);
    }

    @Test
    void stateSnapshot_includesAllStateComponents() {
        runtime.setActiveGoalAndTask(executionId, "goal-42", "task-88");
        runtime.recordObservationReference(executionId, "obs-100");
        runtime.recordUnresolvedQuestion(executionId, "Does API support batching?");
        runtime.update(BeliefUpdateRequest.create(executionId, "batching", "Batching is unknown", List.of()));

        AgentStateSnapshot stateSnapshot = runtime.stateSnapshot(executionId).toCompletableFuture().join();

        assertThat(stateSnapshot.executionId()).isEqualTo(executionId);
        assertThat(stateSnapshot.activeGoalId()).contains("goal-42");
        assertThat(stateSnapshot.activeTaskId()).contains("task-88");
        assertThat(stateSnapshot.observationReferences()).contains("obs-100");
        assertThat(stateSnapshot.unresolvedQuestions()).contains("Does API support batching?");
        assertThat(stateSnapshot.beliefs()).hasSize(1);
        assertThat(stateSnapshot.beliefs().get(0).subject()).isEqualTo("batching");
    }
}
