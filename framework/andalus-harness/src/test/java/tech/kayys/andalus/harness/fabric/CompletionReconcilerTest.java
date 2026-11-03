package tech.kayys.andalus.harness.fabric;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.attempt.AttemptId;
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

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CompletionReconcilerTest {
    private static final Duration LEASE = Duration.ofMinutes(1);

    private record Fixture(
            DefaultLeaseManager leases,
            InMemoryExecutionStateStore states,
            InMemoryExecutionAttemptStore attempts,
            InMemoryCompletionIntentStore intents,
            TaskQueue queue,
            CompletionReconciler reconciler,
            AttemptId attemptId,
            String executionId) {}

    private Fixture fixture() {
        String executionId = "exec-completion";
        DefaultLeaseManager leases = new DefaultLeaseManager();
        InMemoryExecutionStateStore states = new InMemoryExecutionStateStore(leases);
        InMemoryExecutionAttemptStore attempts = new InMemoryExecutionAttemptStore(leases);
        InMemoryCompletionIntentStore intents = new InMemoryCompletionIntentStore();

        var originalLease = leases.acquireLease(executionId, "worker-1", LEASE);
        ExecutionIdentity identity = ExecutionIdentity.of(executionId, "workflow-1", "agent-1");
        ExecutionDurableState initial = ExecutionDurableState.of(identity,
                ExecutionCursor.initial("workflow-1"));
        states.save(initial, originalLease.fencingToken());
        states.save(initial.withStatus(ExecutionStatus.RUNNING), originalLease.fencingToken());

        AttemptId attemptId = AttemptId.of("physical-attempt-1");
        ExecutionAttempt attempt = new ExecutionAttempt(executionId, attemptId, 1,
                AttemptState.RUNNING, "worker-1", java.time.Instant.now(), Optional.empty());
        attempts.save(attempt, new AttemptLineage(executionId, attemptId,
                Optional.empty(), Optional.empty()), originalLease.fencingToken());

        AssignmentId assignmentId = AssignmentId.of("assignment-1");
        TaskClaimToken claim = new TaskClaimToken(assignmentId, WorkerId.of("worker-1"), 7);
        intents.createIfAbsent(new CompletionIntent("completion-1", executionId, attemptId,
                assignmentId, 3, claim, originalLease.fencingToken(),
                CompletionIntentStage.RECORDED, java.time.Instant.now()));
        leases.releaseLease(executionId, "worker-1");

        TaskQueue queue = new TaskQueue() {
            private TaskClaimToken acknowledged;
            @Override public void enqueue(String key, ExecutionAssignment assignment) { throw new UnsupportedOperationException(); }
            @Override public java.util.List<TaskClaim> claim(WorkerId worker, int max, Duration visibility) { throw new UnsupportedOperationException(); }
            @Override public boolean acknowledge(TaskClaimToken token) {
                if (acknowledged != null) return acknowledged.equals(token);
                if (!claim.equals(token)) return false;
                acknowledged = token;
                return true;
            }
            @Override public boolean negativeAcknowledge(TaskClaimToken token, Duration delay) { throw new UnsupportedOperationException(); }
            @Override public boolean extendVisibility(TaskClaimToken token, Duration extension) { throw new UnsupportedOperationException(); }
            @Override public TaskFailureResult fail(TaskClaimToken token, TaskFailureDecision decision) { throw new UnsupportedOperationException(); }
            @Override public java.util.List<DeadLetterTask> listDeadLetters(int limit) { throw new UnsupportedOperationException(); }
        };
        CompletionReconciler reconciler = new CompletionReconciler(
                intents, states, attempts, leases, queue, "reconciler-1", LEASE);
        return new Fixture(leases, states, attempts, intents, queue, reconciler, attemptId, executionId);
    }

    @Test
    void reconcilesBothStoresUsingFreshFenceAndCompletesIntent() {
        Fixture f = fixture();

        assertEquals(CompletionReconciler.Outcome.ACKNOWLEDGED,
                f.reconciler().reconcile("completion-1"));

        assertEquals(ExecutionStatus.COMPLETED,
                f.states().load(f.executionId()).orElseThrow().status());
        assertEquals(AttemptState.COMPLETED,
                f.attempts().get(f.attemptId()).orElseThrow().state());
        assertEquals(CompletionIntentStage.COMPLETED,
                f.intents().get("completion-1").orElseThrow().stage());
        assertTrue(f.leases().getLease(f.executionId()).isEmpty(),
                "reconciliation must release its fresh lease");
    }

    @Test
    void isIdempotentAfterBothWritesButBeforeFinalIntentStage() {
        Fixture f = fixture();
        var lease = f.leases().acquireLease(f.executionId(), "crashed-reconciler", LEASE);
        var state = f.states().load(f.executionId()).orElseThrow();
        f.states().save(state.withStatus(ExecutionStatus.COMPLETED), lease.fencingToken());
        f.attempts().updateState(f.attemptId(), AttemptState.COMPLETED, lease.fencingToken());
        f.leases().releaseLease(f.executionId(), "crashed-reconciler");

        assertEquals(CompletionReconciler.Outcome.ACKNOWLEDGED,
                f.reconciler().reconcile("completion-1"));
        assertEquals(CompletionIntentStage.COMPLETED,
                f.intents().get("completion-1").orElseThrow().stage());
        assertEquals(CompletionReconciler.Outcome.ACKNOWLEDGED,
                f.reconciler().reconcile("completion-1"),
                "retry after acknowledgement must be idempotent");
    }

    @Test
    void defersWhileAnotherWorkerOwnsTheExecutionLease() {
        Fixture f = fixture();
        f.leases().acquireLease(f.executionId(), "active-worker", LEASE);

        assertEquals(CompletionReconciler.Outcome.DEFERRED_ACTIVE_LEASE,
                f.reconciler().reconcile("completion-1"));
        assertEquals(CompletionIntentStage.RECORDED,
                f.intents().get("completion-1").orElseThrow().stage());
    }

    @Test
    void incompleteIntentListingIsBoundedAndOldestFirst() {
        Fixture f = fixture();
        assertEquals(1, f.intents().listIncomplete(1).size());
        assertTrue(f.intents().listIncomplete(1).get(0).idempotencyKey().equals("completion-1"));
        assertThrows(IllegalArgumentException.class, () -> f.intents().listIncomplete(0));
    }
}
