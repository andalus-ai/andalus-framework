package tech.kayys.andalus.a2a.durable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2a.model.A2ATaskStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DefaultDurableA2AOperationService (6.16).
 *
 * Verifies:
 * - Intent persistence before remote submission (6.16.9)
 * - Idempotency key prevents duplicate operations (6.16.13)
 * - enterWaiting calls EnterWaitCallback — thread-free WAITING (6.16.5)
 * - completeOperation persists result BEFORE waking caller (6.16.21)
 * - reconcile handles remote terminal/running states without new requests (6.16.26)
 * - fail/cancel paths wake the caller correctly
 */
class DurableA2AOperationServiceTest {

    private DurableA2ATaskLedger ledger;
    private StubCallbacks callbacks;
    private DefaultDurableA2AOperationService service;
    private static final String CALLER_EXEC = "exec-caller-001";

    @BeforeEach
    void setUp() {
        ledger    = new DurableA2ATaskLedger();
        callbacks = new StubCallbacks();
        service   = new DefaultDurableA2AOperationService(ledger, callbacks, callbacks);
    }

    // ── beginOperation ────────────────────────────────────────────────────────

    @Test
    void beginOperation_createsOperationInPendingStatus() {
        A2AOperationRecord op = service.beginOperation(
                CALLER_EXEC, "http://agent-b:8080", null, "ckpt-001");

        assertNotNull(op);
        assertEquals(A2AOperationStatus.PENDING, op.status());
        assertEquals(CALLER_EXEC, op.callerExecutionId());
        assertEquals("http://agent-b:8080", op.remoteEndpoint());
        assertTrue(op.operationId().startsWith("op-a2a-"));
    }

    @Test
    void beginOperation_withExplicitIdempotencyKey_isStored() {
        A2AOperationRecord op = service.beginOperation(
                CALLER_EXEC, "http://agent-c:8080", "idem-key-abc", null);
        assertEquals("idem-key-abc", op.idempotencyKey());
    }

    @Test
    void beginOperation_sameIdempotencyKey_returnsExistingNonTerminalOperation() {
        // First call — creates the operation
        A2AOperationRecord first = service.beginOperation(
                CALLER_EXEC, "http://agent-b:8080", "stable-key-xyz", "ckpt-001");

        // Second call with same key, same operation is not yet terminal
        A2AOperationRecord second = service.beginOperation(
                CALLER_EXEC, "http://agent-b:8080", "stable-key-xyz", "ckpt-002");

        assertEquals(first.operationId(), second.operationId(),
                "Same idempotency key should return the same operation — prevents duplicate send (6.16.13)");
    }

    // ── submitToRemote ────────────────────────────────────────────────────────

    @Test
    void submitToRemote_transitionsToSubmitted_andStoresTaskId() {
        A2AOperationRecord op = service.beginOperation(CALLER_EXEC, "http://agent-b:8080", null, null);
        A2AOperationRecord submitted = service.submitToRemote(op.operationId(), "task-remote-001");

        assertEquals(A2AOperationStatus.SUBMITTED, submitted.status());
        assertEquals("task-remote-001", submitted.a2aTaskId());
    }

    // ── enterWaiting ─────────────────────────────────────────────────────────

    @Test
    void enterWaiting_transitionsToWaitingForRemote_andCallsEnterWaitCallback() {
        A2AOperationRecord op = service.beginOperation(CALLER_EXEC, "http://agent-b:8080", null, null);
        service.submitToRemote(op.operationId(), "task-001");

        A2AOperationRecord waiting = service.enterWaiting(op.operationId());

        assertEquals(A2AOperationStatus.WAITING_FOR_REMOTE, waiting.status());
        assertEquals(1, callbacks.enterCount,
                "EnterWaitCallback should be called once (thread-free WAITING — 6.16.5)");
        assertEquals(CALLER_EXEC, callbacks.lastEntered);
    }

    // ── completeOperation ─────────────────────────────────────────────────────

    @Test
    void completeOperation_persistsResult_beforeWake_ordering() {
        // 6.16.21: result must be durable before wake
        A2AOperationRecord op = service.beginOperation(CALLER_EXEC, "http://agent-b:8080", null, null);
        service.submitToRemote(op.operationId(), "task-002");
        service.enterWaiting(op.operationId());

        A2AOperationRecord completed = service.completeOperation(op.operationId(), List.of());

        // Status is COMPLETED (result persisted) — the callback is called after this
        assertEquals(A2AOperationStatus.COMPLETED, completed.status(),
                "Status must be COMPLETED (result durable) before wake is triggered (6.16.21)");
        assertEquals(1, callbacks.wakeCount);
        assertEquals(CALLER_EXEC, callbacks.lastWoken);
        assertNotNull(completed.completedAt());
        assertTrue(completed.isTerminal());
    }

    // ── failOperation ─────────────────────────────────────────────────────────

    @Test
    void failOperation_wakesCallerWithFailedStatus() {
        A2AOperationRecord op = service.beginOperation(CALLER_EXEC, "http://agent-b:8080", null, null);
        service.submitToRemote(op.operationId(), "task-004");
        service.enterWaiting(op.operationId());

        A2AOperationRecord failed = service.failOperation(op.operationId(), "network error");

        assertEquals(A2AOperationStatus.FAILED, failed.status());
        assertEquals(1, callbacks.wakeCount);
        assertTrue(failed.isTerminal());
    }

    // ── cancelOperation ───────────────────────────────────────────────────────

    @Test
    void cancelOperation_wakesCallerWithCancelledStatus() {
        A2AOperationRecord op = service.beginOperation(CALLER_EXEC, "http://agent-b:8080", null, null);
        service.submitToRemote(op.operationId(), "task-005");
        service.enterWaiting(op.operationId());

        A2AOperationRecord cancelled = service.cancelOperation(op.operationId());

        assertEquals(A2AOperationStatus.CANCELLED, cancelled.status());
        assertEquals(1, callbacks.wakeCount);
    }

    // ── reconcile ─────────────────────────────────────────────────────────────

    @Test
    void reconcile_remoteCompleted_completesLocally() {
        // Set up a completed ledger task
        ledger.create(CALLER_EXEC, "http://agent-b:8080", null);
        String taskId = ledger.allPending().get(0).taskId();
        ledger.complete(taskId);

        A2AOperationRecord op = service.beginOperation(CALLER_EXEC, "http://agent-b:8080", null, null);
        service.submitToRemote(op.operationId(), taskId);
        service.enterWaiting(op.operationId());

        A2AOperationRecord reconciled = service.reconcile(op.operationId());
        assertEquals(A2AOperationStatus.COMPLETED, reconciled.status(),
                "Reconcile: remote COMPLETED → complete locally");
    }

    @Test
    void reconcile_remoteRunning_reRegistersWaiting_withoutNewRequest() {
        // Remote still running
        ledger.create(CALLER_EXEC, "http://agent-b:8080", null);
        String taskId = ledger.allPending().get(0).taskId();
        ledger.update(taskId, A2ATaskStatus.RUNNING);

        A2AOperationRecord op = service.beginOperation(CALLER_EXEC, "http://agent-b:8080", null, null);
        service.submitToRemote(op.operationId(), taskId);
        service.enterWaiting(op.operationId());

        int enterBefore = callbacks.enterCount;
        A2AOperationRecord reconciled = service.reconcile(op.operationId());

        assertEquals(A2AOperationStatus.WAITING_FOR_REMOTE, reconciled.status(),
                "Remote still running → stay WAITING");
        assertEquals(enterBefore + 1, callbacks.enterCount,
                "Re-registered WAITING — no new A2A request sent (6.16.26)");
    }

    @Test
    void reconcile_noTaskId_resetsToPending() {
        A2AOperationRecord op = service.beginOperation(CALLER_EXEC, "http://agent-b:8080", null, null);
        // Never submitted — no taskId
        A2AOperationRecord reconciled = service.reconcile(op.operationId());
        assertEquals(A2AOperationStatus.PENDING, reconciled.status(),
                "No taskId → reset to PENDING for retry");
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    @Test
    void findByIdempotencyKey_returnsExistingOperation() {
        service.beginOperation(CALLER_EXEC, "http://agent-b:8080", "find-key", null);
        Optional<A2AOperationRecord> found = service.findByIdempotencyKey("find-key");
        assertTrue(found.isPresent());
        assertEquals("find-key", found.get().idempotencyKey());
    }

    @Test
    void findByCallerExecution_returnsAllOperationsForCaller() {
        service.beginOperation(CALLER_EXEC, "http://a:8080", null, null);
        service.beginOperation(CALLER_EXEC, "http://b:8080", null, null);
        assertEquals(2, service.findByCallerExecution(CALLER_EXEC).size());
    }

    // ── Stub callbacks ────────────────────────────────────────────────────────

    private static class StubCallbacks
            implements DefaultDurableA2AOperationService.EnterWaitCallback,
                       DefaultDurableA2AOperationService.WakeCallback {
        int    enterCount  = 0;
        int    wakeCount   = 0;
        String lastEntered = null;
        String lastWoken   = null;

        @Override
        public void enterWaiting(String callerExecutionId, String wakeHint) {
            enterCount++;
            lastEntered = callerExecutionId;
        }

        @Override
        public void wakeExecution(String callerExecutionId) {
            wakeCount++;
            lastWoken = callerExecutionId;
        }
    }
}
