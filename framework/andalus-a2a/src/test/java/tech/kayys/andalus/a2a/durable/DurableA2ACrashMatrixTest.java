package tech.kayys.andalus.a2a.durable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2a.model.A2ATaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Crash-window and restart tests for durable A2A (6.16.8.17–8.32).
 *
 * <p>Restart simulation follows 6.16.8.33: the <b>durable</b> parts (ledger, remote
 * fake) survive; the <b>transient</b> parts (wake registrations, bridge,
 * reconciliation service) are rebuilt from scratch via {@link #restart()}.</p>
 */
class DurableA2ACrashMatrixTest {

    /** Durable: survives "restart". */
    private DurableA2ATaskLedger ledger;
    private FakeA2AClient remote;
    /** Captured wakes (stand-in for the durable-validated DurableWaitController). */
    private List<String> wakes;

    @BeforeEach
    void setUp() {
        ledger = new DurableA2ATaskLedger();
        remote = new FakeA2AClient();
        wakes = new ArrayList<>();
    }

    /** Builds fresh transient components over the surviving durable state. */
    private DefaultA2AReconciliationService restart() {
        A2AWakeCoordinator wakeCoordinator = new DefaultA2AWakeCoordinator(ledger, wakes::add);
        A2ACompletionBridge bridge = new DefaultA2ACompletionBridge(ledger, wakeCoordinator);
        return new DefaultA2AReconciliationService(
                ledger, bridge, new A2AClientTaskReconciler(remote), remote);
    }

    @Test
    void crashBeforeRemoteSubmission_notFound_noBlindResubmission() {
        // Window: intent persisted locally, remote never received it (6.16.8.17).
        DurableA2ATask task = ledger.createForCaller("exec-a", "http://remote/agent", "ckpt-1");
        remote.droppedResponses.add(task.taskId());

        A2ARecoveryDecision decision = restart().reconcileTask(task.taskId()).toCompletableFuture().join();

        assertEquals(A2ARecoveryDecision.NOT_FOUND, decision);
        assertTrue(remote.sentMessages.isEmpty(), "recovery must never blindly resubmit");
        assertFalse(ledger.find(task.taskId()).orElseThrow().isTerminal(),
                "local task stays pending for the submission policy to evaluate");
        assertTrue(wakes.isEmpty());
    }

    @Test
    void crashDuringRemoteSubmission_unknown_retriedNotResubmitted() {
        // Window: request may or may not have reached the remote (6.16.8.18).
        DurableA2ATask task = ledger.createForCaller("exec-b", "http://remote/agent", "ckpt-1");
        remote.networkErrors.add(task.taskId());

        assertEquals(A2ARecoveryDecision.UNKNOWN,
                restart().reconcileTask(task.taskId()).toCompletableFuture().join());
        assertTrue(remote.sentMessages.isEmpty());

        // Network heals; the remote actually did accept it before the crash.
        remote.networkErrors.remove(task.taskId());
        remote.remoteState.put(task.taskId(), A2ATaskStatus.RUNNING);

        assertEquals(A2ARecoveryDecision.FOUND_RUNNING,
                restart().reconcileTask(task.taskId()).toCompletableFuture().join());
        assertEquals(A2ATaskStatus.RUNNING, ledger.find(task.taskId()).orElseThrow().status());
        assertTrue(remote.sentMessages.isEmpty(), "reconciliation never resubmits");
        assertTrue(wakes.isEmpty(), "still-running remote must not wake the caller");
    }

    @Test
    void crashAfterRemoteAcceptance_completionRecoveredAndWakes() {
        // Window: remote accepted and later completed while local was down (6.16.8.19).
        DurableA2ATask task = ledger.createForCaller("exec-c", "http://remote/agent", "ckpt-1");
        ledger.update(task.taskId(), A2ATaskStatus.RUNNING);
        remote.remoteState.put(task.taskId(), A2ATaskStatus.COMPLETED);

        A2ARecoveryDecision decision = restart().reconcileTask(task.taskId()).toCompletableFuture().join();

        assertEquals(A2ARecoveryDecision.FOUND_COMPLETED, decision);
        assertEquals(A2ATaskStatus.COMPLETED, ledger.find(task.taskId()).orElseThrow().status());
        assertEquals(List.of("exec-c"), wakes);
    }

    @Test
    void lostCompletionNotification_recoveredByRestartReconciliation() {
        // 6.16.8.20: completion callback never arrived; restart + getTask recovers it.
        DurableA2ATask task = ledger.createForCaller("exec-d", "http://remote/agent", null);
        ledger.update(task.taskId(), A2ATaskStatus.RUNNING);
        remote.remoteState.put(task.taskId(), A2ATaskStatus.COMPLETED);

        // restart wipes transient registrations; reconciliation rediscovers the outcome
        restart().reconcile("exec-d").toCompletableFuture().join();

        assertEquals(A2ATaskStatus.COMPLETED, ledger.find(task.taskId()).orElseThrow().status());
        assertEquals(List.of("exec-d"), wakes);
        assertTrue(remote.polledTasks.contains(task.taskId()));
    }

    @Test
    void remoteFailedDuringDowntime_failurePersistedAndWoken() {
        DurableA2ATask task = ledger.createForCaller("exec-e", "http://remote/agent", null);
        remote.remoteState.put(task.taskId(), A2ATaskStatus.FAILED);

        assertEquals(A2ARecoveryDecision.FOUND_FAILED,
                restart().reconcileTask(task.taskId()).toCompletableFuture().join());
        assertEquals(A2ATaskStatus.FAILED, ledger.find(task.taskId()).orElseThrow().status());
        assertEquals(List.of("exec-e"), wakes);
    }

    @Test
    void remoteCancelledDuringDowntime_cancellationPersistedAndWoken() {
        DurableA2ATask task = ledger.createForCaller("exec-f", "http://remote/agent", null);
        remote.remoteState.put(task.taskId(), A2ATaskStatus.CANCELLED);

        assertEquals(A2ARecoveryDecision.FOUND_CANCELLED,
                restart().reconcileTask(task.taskId()).toCompletableFuture().join());
        assertEquals(A2ATaskStatus.CANCELLED, ledger.find(task.taskId()).orElseThrow().status());
        assertEquals(List.of("exec-f"), wakes);
    }

    @Test
    void multipleTasksOfOneExecution_areIsolated() {
        // 6.16.8.29: one execution with several A2A tasks — outcomes don't bleed across.
        DurableA2ATask t1 = ledger.createForCaller("exec-g", "http://remote/agent", null);
        DurableA2ATask t2 = ledger.createForCaller("exec-g", "http://remote/agent", null);
        remote.remoteState.put(t1.taskId(), A2ATaskStatus.COMPLETED);
        remote.remoteState.put(t2.taskId(), A2ATaskStatus.RUNNING);

        List<A2ARecoveryDecision> decisions =
                restart().reconcile("exec-g").toCompletableFuture().join();

        assertEquals(2, decisions.size());
        assertEquals(A2ATaskStatus.COMPLETED, ledger.find(t1.taskId()).orElseThrow().status());
        assertEquals(A2ATaskStatus.RUNNING, ledger.find(t2.taskId()).orElseThrow().status());
    }

    @Test
    void tenantScopedCallers_areIsolated() {
        // 6.16.8.32: caller scoping prevents cross-tenant observation even when the
        // remote endpoints are identical.
        DurableA2ATask tenantA = ledger.createForCaller("tenant-a:exec-1", "http://remote/agent", null);
        DurableA2ATask tenantB = ledger.createForCaller("tenant-b:exec-1", "http://remote/agent", null);
        remote.remoteState.put(tenantA.taskId(), A2ATaskStatus.COMPLETED);
        remote.remoteState.put(tenantB.taskId(), A2ATaskStatus.COMPLETED);

        restart().reconcile("tenant-a:exec-1").toCompletableFuture().join();

        assertEquals(A2ATaskStatus.COMPLETED, ledger.find(tenantA.taskId()).orElseThrow().status());
        assertEquals(A2ATaskStatus.PENDING, ledger.find(tenantB.taskId()).orElseThrow().status(),
                "tenant B's task must not be touched by tenant A's reconciliation");
        assertEquals(List.of("tenant-a:exec-1"), wakes);
        assertTrue(ledger.findForCallerExecution("tenant-b:exec-1", tenantA.taskId()).isEmpty());
    }
}
