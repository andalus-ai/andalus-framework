package tech.kayys.andalus.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.attempt.AttemptLineage;
import tech.kayys.andalus.execution.attempt.AttemptState;
import tech.kayys.andalus.execution.attempt.ExecutionAttempt;
import tech.kayys.andalus.execution.core.attempt.InMemoryExecutionAttemptStore;
import tech.kayys.andalus.execution.core.checkpoint.InMemoryExecutionCheckpointStore;
import tech.kayys.andalus.execution.core.mobility.DefaultLeaseManager;
import tech.kayys.andalus.execution.core.mobility.InMemoryExecutionStateStore;
import tech.kayys.andalus.execution.core.recovery.DefaultRecoveryManager;
import tech.kayys.andalus.execution.core.recovery.DefaultRecoveryReconciler;
import tech.kayys.andalus.execution.mobility.ExecutionCursor;
import tech.kayys.andalus.execution.mobility.ExecutionDurableState;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.mobility.ExecutionStatus;
import tech.kayys.andalus.execution.recovery.RecoveryReconciliationAction;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RecoveryReconciliationTest {

    @Test
    void marksAbandonedInitializingAttemptLostAndSuspendsExecution() {
        Fixture fixture = new Fixture("reconcile-abandoned");
        DefaultRecoveryManager manager = new DefaultRecoveryManager(new InMemoryExecutionCheckpointStore(),
                fixture.leases, fixture.states, fixture.attempts, "recovery-worker", Duration.ofMinutes(1));
        var plan = manager.plan(fixture.executionId, manager.assess(fixture.executionId));
        assertTrue(manager.recover(plan).success());
        assertEquals(ExecutionStatus.RECOVERING, fixture.states.load(fixture.executionId).orElseThrow().status());
        assertEquals(AttemptState.INITIALIZING, fixture.attempts.getLatest(fixture.executionId).orElseThrow().state());

        fixture.leases.releaseLease(fixture.executionId, "recovery-worker");
        var result = fixture.reconciler().reconcile(fixture.executionId);

        assertEquals(RecoveryReconciliationAction.MARKED_SUSPENDED, result.action());
        assertEquals(AttemptState.LOST, fixture.attempts.getLatest(fixture.executionId).orElseThrow().state());
        assertEquals(ExecutionStatus.SUSPENDED, fixture.states.load(fixture.executionId).orElseThrow().status());
    }

    @Test
    void promotesResumingExecutionWhenAttemptRunningWriteAlreadyCommitted() {
        Fixture fixture = new Fixture("reconcile-promote");
        var lease = fixture.leases.acquireLease(fixture.executionId, "recovery-worker", Duration.ofMinutes(1));
        ExecutionDurableState state = fixture.states.load(fixture.executionId).orElseThrow()
                .withStatus(ExecutionStatus.RECOVERING)
                .withStatus(ExecutionStatus.RESUMING);
        fixture.states.save(state, lease.fencingToken());

        AttemptId attemptId = AttemptId.random();
        ExecutionAttempt attempt = new ExecutionAttempt(fixture.executionId, attemptId, 1,
                AttemptState.RUNNING, "recovery-worker", Instant.now(), Optional.empty());
        fixture.attempts.save(attempt, new AttemptLineage(fixture.executionId, attemptId,
                Optional.empty(), Optional.empty()), lease.fencingToken());
        fixture.leases.releaseLease(fixture.executionId, "recovery-worker");

        var result = fixture.reconciler().reconcile(fixture.executionId);

        assertEquals(RecoveryReconciliationAction.PROMOTED_RUNNING, result.action());
        assertEquals(ExecutionStatus.RUNNING, fixture.states.load(fixture.executionId).orElseThrow().status());
        assertEquals(AttemptState.RUNNING, fixture.attempts.getLatest(fixture.executionId).orElseThrow().state());
    }

    @Test
    void defersWhileRecoveryLeaseIsStillActive() {
        Fixture fixture = new Fixture("reconcile-active");
        var lease = fixture.leases.acquireLease(fixture.executionId, "recovery-worker", Duration.ofMinutes(1));
        fixture.states.save(fixture.states.load(fixture.executionId).orElseThrow()
                .withStatus(ExecutionStatus.RECOVERING), lease.fencingToken());

        var result = fixture.reconciler().reconcile(fixture.executionId);

        assertEquals(RecoveryReconciliationAction.ACTIVE_RECOVERY, result.action());
        assertEquals(ExecutionStatus.RECOVERING, fixture.states.load(fixture.executionId).orElseThrow().status());
    }

    private static final class Fixture {
        private final String executionId;
        private final DefaultLeaseManager leases = new DefaultLeaseManager();
        private final InMemoryExecutionStateStore states = new InMemoryExecutionStateStore(leases);
        private final InMemoryExecutionAttemptStore attempts = new InMemoryExecutionAttemptStore(leases);

        private Fixture(String executionId) {
            this.executionId = executionId;
            var lease = leases.acquireLease(executionId, "initial-worker", Duration.ofMinutes(1));
            states.save(ExecutionDurableState.of(ExecutionIdentity.of(executionId, "workflow", "agent"),
                    ExecutionCursor.initial("workflow")), lease.fencingToken());
            leases.releaseLease(executionId, "initial-worker");
        }

        private DefaultRecoveryReconciler reconciler() {
            return new DefaultRecoveryReconciler(leases, states, attempts,
                    "reconciler-worker", Duration.ofMinutes(1));
        }
    }
}
