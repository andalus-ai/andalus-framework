package tech.kayys.andalus.harness.fabric;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.attempt.AttemptLineage;
import tech.kayys.andalus.execution.attempt.AttemptState;
import tech.kayys.andalus.execution.attempt.ExecutionAttempt;
import tech.kayys.andalus.execution.core.attempt.InMemoryExecutionAttemptStore;
import tech.kayys.andalus.execution.core.mobility.DefaultLeaseManager;
import tech.kayys.andalus.execution.core.mobility.InMemoryExecutionStateStore;
import tech.kayys.andalus.execution.mobility.ExecutionCursor;
import tech.kayys.andalus.execution.mobility.ExecutionDurableState;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.mobility.ExecutionStatus;
import tech.kayys.andalus.workflow.graph.NodeId;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CompletedRedeliveryAcknowledgementTest {
    private static final String EXECUTION_ID = "redelivered-execution";
    private static final String INTENT_KEY = "completion-redelivery";
    private static final Duration LEASE = Duration.ofMinutes(1);

    private record Assignment(
            AssignmentId id,
            tech.kayys.andalus.harness.consistency.state.ExecutionId executionId,
            NodeId nodeId,
            AttemptId attemptId,
            WorkerId workerId,
            AssignmentFence fence,
            AssignmentStatus status,
            AssignmentConstraints constraints) implements ExecutionAssignment {}

    private record Fixture(
            CompletionReconciler reconciler,
            InMemoryCompletionIntentStore intents,
            InMemoryExecutionStateStore states,
            InMemoryExecutionAttemptStore attempts,
            AtomicReference<TaskClaimToken> acknowledged,
            TaskClaim claim) {}

    private Fixture fixture(boolean completed, boolean includeIntent) {
        DefaultLeaseManager leases = new DefaultLeaseManager();
        InMemoryExecutionStateStore states = new InMemoryExecutionStateStore(leases);
        InMemoryExecutionAttemptStore attempts = new InMemoryExecutionAttemptStore(leases);
        InMemoryCompletionIntentStore intents = new InMemoryCompletionIntentStore();
        var lease = leases.acquireLease(EXECUTION_ID, "setup", LEASE);
        var identity = ExecutionIdentity.of(EXECUTION_ID, "workflow-redelivery", "agent-redelivery");
        var state = ExecutionDurableState.of(identity, ExecutionCursor.initial("workflow-redelivery"));
        states.save(state, lease.fencingToken());
        states.save(state.withStatus(completed ? ExecutionStatus.COMPLETED : ExecutionStatus.RUNNING),
                lease.fencingToken());

        var physicalAttempt = tech.kayys.andalus.execution.attempt.AttemptId.of("physical-redelivery-1");
        var attempt = new ExecutionAttempt(EXECUTION_ID, physicalAttempt, 1,
                AttemptState.RUNNING, "worker-old", Instant.now(), Optional.empty());
        attempts.save(attempt, new AttemptLineage(EXECUTION_ID, physicalAttempt,
                Optional.empty(), Optional.empty()), lease.fencingToken());
        if (completed) {
            attempts.updateState(physicalAttempt, AttemptState.COMPLETED, lease.fencingToken());
        }
        leases.releaseLease(EXECUTION_ID, "setup");

        AssignmentId assignmentId = AssignmentId.of("assignment-redelivery");
        TaskClaimToken oldToken = new TaskClaimToken(assignmentId, WorkerId.of("worker-old"), 7);
        if (includeIntent) {
            intents.createIfAbsent(new CompletionIntent(INTENT_KEY, EXECUTION_ID, physicalAttempt,
                    assignmentId, 3, oldToken, lease.fencingToken(),
                    completed ? CompletionIntentStage.COMPLETED : CompletionIntentStage.RECORDED,
                    Instant.now()));
        }

        Assignment assignment = new Assignment(assignmentId,
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(EXECUTION_ID),
                NodeId.of("node-redelivery"), AttemptId.initial(), WorkerId.of("worker-new"),
                AssignmentFence.of(assignmentId, 3), AssignmentStatus.DISPATCHED,
                AssignmentConstraints.defaults());
        TaskClaimToken newToken = new TaskClaimToken(assignmentId, WorkerId.of("worker-new"), 8);
        TaskClaim claim = new TaskClaim(assignment, newToken, Instant.now(), Instant.now().plusSeconds(30));
        AtomicReference<TaskClaimToken> acknowledged = new AtomicReference<>();
        TaskQueue queue = new TaskQueue() {
            @Override public void enqueue(String key, ExecutionAssignment value) { throw new UnsupportedOperationException(); }
            @Override public List<TaskClaim> claim(WorkerId worker, int max, Duration visibility) { throw new UnsupportedOperationException(); }
            @Override public boolean acknowledge(TaskClaimToken token) {
                if (!newToken.equals(token)) return false;
                return acknowledged.compareAndSet(null, token) || token.equals(acknowledged.get());
            }
            @Override public boolean negativeAcknowledge(TaskClaimToken token, Duration delay) { throw new UnsupportedOperationException(); }
            @Override public boolean extendVisibility(TaskClaimToken token, Duration extension) { throw new UnsupportedOperationException(); }
            @Override public TaskFailureResult fail(TaskClaimToken token, TaskFailureDecision decision) { throw new UnsupportedOperationException(); }
            @Override public List<DeadLetterTask> listDeadLetters(int limit) { throw new UnsupportedOperationException(); }
        };
        CompletionReconciler reconciler = new CompletionReconciler(
                intents, states, attempts, leases, queue, "redelivery-reconciler", LEASE);
        return new Fixture(reconciler, intents, states, attempts, acknowledged, claim);
    }

    @Test
    void acknowledgesNewClaimWhenBothDurableRecordsConfirmCompletion() {
        Fixture f = fixture(true, true);

        assertEquals(CompletionReconciler.Outcome.ACKNOWLEDGED,
                f.reconciler().acknowledgeCompletedRedelivery(f.claim()));
        assertEquals(f.claim().token(), f.acknowledged().get());
        assertEquals(CompletionIntentStage.COMPLETED,
                f.intents().get(INTENT_KEY).orElseThrow().stage(),
                "redelivery acknowledgement does not rewrite the original intent history");
    }

    @Test
    void doesNotAcknowledgeWhenEitherDurableCompletionRecordIsIncomplete() {
        Fixture f = fixture(false, true);

        assertEquals(CompletionReconciler.Outcome.COMPLETION_NOT_CONFIRMED,
                f.reconciler().acknowledgeCompletedRedelivery(f.claim()));
        assertNull(f.acknowledged().get());
    }

    @Test
    void returnsNoMatchWhenAssignmentHasNoCompletionIntent() {
        Fixture f = fixture(true, false);

        assertEquals(CompletionReconciler.Outcome.NO_MATCHING_COMPLETION,
                f.reconciler().acknowledgeCompletedRedelivery(f.claim()));
        assertNull(f.acknowledged().get());
    }

    @Test
    void rejectsAssignmentFenceMismatch() {
        Fixture f = fixture(true, true);
        Assignment original = (Assignment) f.claim().assignment();
        Assignment mismatched = new Assignment(original.id(), original.executionId(), original.nodeId(),
                original.attemptId(), original.workerId(),
                AssignmentFence.of(original.id(), 99), original.status(), original.constraints());
        TaskClaim badClaim = new TaskClaim(mismatched, f.claim().token(),
                f.claim().claimedAt(), f.claim().visibilityExpiresAt());

        assertEquals(CompletionReconciler.Outcome.ASSIGNMENT_MISMATCH,
                f.reconciler().acknowledgeCompletedRedelivery(badClaim));
        assertNull(f.acknowledged().get());
    }

    @Test
    void duplicateAcknowledgementOfSameRedeliveryClaimIsIdempotent() {
        Fixture f = fixture(true, true);

        assertEquals(CompletionReconciler.Outcome.ACKNOWLEDGED,
                f.reconciler().acknowledgeCompletedRedelivery(f.claim()));
        assertEquals(CompletionReconciler.Outcome.ACKNOWLEDGED,
                f.reconciler().acknowledgeCompletedRedelivery(f.claim()));
    }
}
