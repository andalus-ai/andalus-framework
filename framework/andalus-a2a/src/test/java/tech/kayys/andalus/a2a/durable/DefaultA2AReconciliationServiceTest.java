package tech.kayys.andalus.a2a.durable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2a.api.A2AClient;
import tech.kayys.andalus.a2a.model.A2AMessage;
import tech.kayys.andalus.a2a.model.A2ATask;
import tech.kayys.andalus.a2a.model.A2ATaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for 6.16.6 reconciliation and 6.16.7 cancellation propagation:
 * decision mapping, idempotency, no blind resubmission, non-blocking remote cancel.
 */
class DefaultA2AReconciliationServiceTest {

    private DurableA2ATaskLedger ledger;
    private List<String> wakes;
    private FakeA2AClient client;
    private DefaultA2AReconciliationService service;

    @BeforeEach
    void setUp() {
        ledger = new DurableA2ATaskLedger();
        wakes = new ArrayList<>();
        client = new FakeA2AClient();
        A2AWakeCoordinator wakeCoordinator = new DefaultA2AWakeCoordinator(ledger, wakes::add);
        A2ACompletionBridge bridge = new DefaultA2ACompletionBridge(ledger, wakeCoordinator);
        service = new DefaultA2AReconciliationService(
                ledger, bridge, new A2AClientTaskReconciler(client), client);
    }

    // ── 6.16.6 — reconciliation ───────────────────────────────────────────────

    @Test
    void reconcile_completed_task_persists_and_wakes() {
        DurableA2ATask task = ledger.createForCaller("exec-1", "http://remote/agent", null);
        ledger.update(task.taskId(), A2ATaskStatus.RUNNING);
        client.remoteStatus.put(task.taskId(), A2ATaskStatus.COMPLETED);

        A2ARecoveryDecision decision = service.reconcileTask(task.taskId()).toCompletableFuture().join();

        assertEquals(A2ARecoveryDecision.FOUND_COMPLETED, decision);
        assertEquals(A2ATaskStatus.COMPLETED, ledger.find(task.taskId()).orElseThrow().status());
        assertEquals(List.of("exec-1"), wakes);
    }

    @Test
    void reconcile_failed_and_cancelled_wake_the_caller() {
        DurableA2ATask t1 = ledger.createForCaller("exec-2", "http://remote/agent", null);
        DurableA2ATask t2 = ledger.createForCaller("exec-3", "http://remote/agent", null);
        client.remoteStatus.put(t1.taskId(), A2ATaskStatus.FAILED);
        client.remoteStatus.put(t2.taskId(), A2ATaskStatus.CANCELLED);

        assertEquals(A2ARecoveryDecision.FOUND_FAILED,
                service.reconcileTask(t1.taskId()).toCompletableFuture().join());
        assertEquals(A2ARecoveryDecision.FOUND_CANCELLED,
                service.reconcileTask(t2.taskId()).toCompletableFuture().join());

        assertEquals(A2ATaskStatus.FAILED, ledger.find(t1.taskId()).orElseThrow().status());
        assertEquals(A2ATaskStatus.CANCELLED, ledger.find(t2.taskId()).orElseThrow().status());
        assertEquals(List.of("exec-2", "exec-3"), wakes);
    }

    @Test
    void reconcile_running_task_never_resubmits() {
        DurableA2ATask task = ledger.createForCaller("exec-4", "http://remote/agent", null);
        ledger.update(task.taskId(), A2ATaskStatus.RUNNING);
        client.remoteStatus.put(task.taskId(), A2ATaskStatus.RUNNING);

        A2ARecoveryDecision decision = service.reconcileTask(task.taskId()).toCompletableFuture().join();

        assertEquals(A2ARecoveryDecision.FOUND_RUNNING, decision);
        assertEquals(1, ledger.taskCount(), "reconciliation must never create new tasks");
        assertTrue(wakes.isEmpty(), "a still-running remote task must not wake the caller");
    }

    @Test
    void reconcile_is_idempotent_for_terminal_remote_state() {
        DurableA2ATask task = ledger.createForCaller("exec-5", "http://remote/agent", null);
        client.remoteStatus.put(task.taskId(), A2ATaskStatus.COMPLETED);

        service.reconcileTask(task.taskId()).toCompletableFuture().join();
        service.reconcileTask(task.taskId()).toCompletableFuture().join();
        service.reconcileTask(task.taskId()).toCompletableFuture().join();

        assertEquals(A2ATaskStatus.COMPLETED, ledger.find(task.taskId()).orElseThrow().status());
        assertEquals(1, ledger.taskCount(), "repeated reconciliation must not resubmit");
    }

    @Test
    void not_found_is_distinct_from_failure_and_leaves_state_untouched() {
        DurableA2ATask task = ledger.createForCaller("exec-6", "http://remote/agent", null);
        ledger.update(task.taskId(), A2ATaskStatus.RUNNING);
        client.unknownTasks.add(task.taskId());

        A2ARecoveryDecision decision = service.reconcileTask(task.taskId()).toCompletableFuture().join();

        assertEquals(A2ARecoveryDecision.NOT_FOUND, decision);
        assertEquals(A2ATaskStatus.RUNNING, ledger.find(task.taskId()).orElseThrow().status());
        assertTrue(wakes.isEmpty());
    }

    @Test
    void transport_error_yields_unknown_and_leaves_state_untouched() {
        DurableA2ATask task = ledger.createForCaller("exec-7", "http://remote/agent", null);
        ledger.update(task.taskId(), A2ATaskStatus.RUNNING);
        client.failingTasks.add(task.taskId());

        A2ARecoveryDecision decision = service.reconcileTask(task.taskId()).toCompletableFuture().join();

        assertEquals(A2ARecoveryDecision.UNKNOWN, decision);
        assertEquals(A2ATaskStatus.RUNNING, ledger.find(task.taskId()).orElseThrow().status());
    }

    @Test
    void reconcile_unknown_local_task_is_not_found() {
        assertEquals(A2ARecoveryDecision.NOT_FOUND,
                service.reconcileTask("no-such-task").toCompletableFuture().join());
    }

    @Test
    void reconcile_by_execution_covers_only_its_pending_tasks() {
        DurableA2ATask mine = ledger.createForCaller("exec-8", "http://remote/agent", null);
        DurableA2ATask other = ledger.createForCaller("exec-9", "http://remote/agent", null);
        DurableA2ATask done = ledger.createForCaller("exec-8", "http://remote/agent", null);
        ledger.complete(done.taskId());
        client.remoteStatus.put(mine.taskId(), A2ATaskStatus.COMPLETED);
        client.remoteStatus.put(other.taskId(), A2ATaskStatus.RUNNING);

        List<A2ARecoveryDecision> decisions = service.reconcile("exec-8").toCompletableFuture().join();

        assertEquals(List.of(A2ARecoveryDecision.FOUND_COMPLETED), decisions);
        assertEquals(A2ATaskStatus.COMPLETED, ledger.find(mine.taskId()).orElseThrow().status());
        assertEquals(A2ATaskStatus.PENDING, ledger.find(other.taskId()).orElseThrow().status(),
                "another execution's tasks must not be touched");
    }

    // -- 6.16.7 -- cancellation propagation -----------------------------------

    @Test
    void cancelPendingTasks_issues_remote_cancel_and_settles_task() {
        DurableA2ATask task = ledger.createForCaller("exec-10", "http://remote/agent", null);
        ledger.update(task.taskId(), A2ATaskStatus.RUNNING);

        int issued = service.cancelPendingTasks("exec-10").toCompletableFuture().join();

        assertEquals(1, issued);
        assertEquals(List.of(task.taskId()), client.cancelledTasks);
        assertEquals(A2ATaskStatus.CANCELLED, ledger.find(task.taskId()).orElseThrow().status());
    }

    @Test
    void cancelPendingTasks_failure_leaves_task_for_reconciliation() {
        DurableA2ATask task = ledger.createForCaller("exec-11", "http://remote/agent", null);
        ledger.update(task.taskId(), A2ATaskStatus.RUNNING);
        client.cancelFailures.add(task.taskId());

        int issued = service.cancelPendingTasks("exec-11").toCompletableFuture().join();

        assertEquals(1, issued);
        assertEquals(A2ATaskStatus.RUNNING, ledger.find(task.taskId()).orElseThrow().status(),
                "remote cancellation failure must not mutate local task state");
    }

    @Test
    void cancelPendingTasks_skips_terminal_tasks() {
        DurableA2ATask task = ledger.createForCaller("exec-12", "http://remote/agent", null);
        ledger.complete(task.taskId());

        int issued = service.cancelPendingTasks("exec-12").toCompletableFuture().join();

        assertEquals(0, issued);
        assertTrue(client.cancelledTasks.isEmpty());
    }

    // -- Fake transport --------------------------------------------------------

    private static final class FakeA2AClient implements A2AClient {
        final Map<String, A2ATaskStatus> remoteStatus = new HashMap<>();
        final List<String> unknownTasks = new ArrayList<>();
        final List<String> failingTasks = new ArrayList<>();
        final List<String> cancelledTasks = new ArrayList<>();
        final List<String> cancelFailures = new ArrayList<>();

        @Override
        public CompletionStage<A2ATask> sendMessage(A2AMessage message) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompletionStage<A2ATask> getTask(String taskId) {
            if (unknownTasks.contains(taskId)) {
                return CompletableFuture.failedFuture(new IllegalArgumentException("Task not found: " + taskId));
            }
            if (failingTasks.contains(taskId)) {
                return CompletableFuture.failedFuture(new RuntimeException("connection refused"));
            }
            A2ATaskStatus status = remoteStatus.get(taskId);
            if (status == null) {
                return CompletableFuture.failedFuture(new IllegalArgumentException("Task not found: " + taskId));
            }
            return CompletableFuture.completedFuture(
                    new A2ATask(taskId, "exec-remote-1", status, List.of(), Map.of()));
        }

        @Override
        public CompletionStage<Void> cancelTask(String taskId) {
            if (cancelFailures.contains(taskId)) {
                return CompletableFuture.failedFuture(new RuntimeException("cancel rejected"));
            }
            cancelledTasks.add(taskId);
            return CompletableFuture.completedFuture(null);
        }
    }
}
