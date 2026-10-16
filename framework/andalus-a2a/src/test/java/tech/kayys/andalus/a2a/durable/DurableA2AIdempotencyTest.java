package tech.kayys.andalus.a2a.durable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2a.model.A2ATaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Idempotency and replay tests for durable A2A (6.16.8.24/8.25):
 * keyed replay preserves the IdempotencyKey, replay preserves the OperationId,
 * and non-idempotent/unknown operations are never blindly replayed.
 */
class DurableA2AIdempotencyTest {

    private DurableA2ATaskLedger ledger;
    private FakeA2AClient remote;

    @BeforeEach
    void setUp() {
        ledger = new DurableA2ATaskLedger();
        remote = new FakeA2AClient();
    }

    private DefaultA2AReconciliationService serviceWith(A2AReconciler reconciler) {
        return new DefaultA2AReconciliationService(
                ledger,
                new DefaultA2ACompletionBridge(ledger, new DefaultA2AWakeCoordinator(ledger, id -> {})),
                reconciler,
                remote);
    }

    private static A2AReconciler capturing(List<A2AInvocationIntent> captured) {
        return (intent, task) -> {
            captured.add(intent);
            return CompletableFuture.completedFuture(A2ARecoveryDecision.FOUND_RUNNING);
        };
    }

    @Test
    void createFromIntent_propagates_durable_identity_to_metadata() {
        A2AInvocationIntent intent = A2AInvocationIntent.keyed(
                "exec-1", "op-42", "task-x", "http://remote/agent", "idem-key-1");

        DurableA2ATask task = ledger.createFromIntent(intent, "ckpt-1");

        assertEquals("task-x", task.taskId(), "intent-driven creation uses the stable task id");
        assertEquals("exec-1", task.callerExecutionId());
        assertEquals("op-42", task.metadata().get(A2AInvocationIntent.META_OPERATION_ID));
        assertEquals("idem-key-1", task.metadata().get(A2AInvocationIntent.META_IDEMPOTENCY_KEY));
        assertEquals("exec-1", task.metadata().get(A2AInvocationIntent.META_EXECUTION_ID));
        assertEquals("task-x", task.metadata().get(A2AInvocationIntent.META_A2A_TASK_ID));
    }

    @Test
    void createFromIntent_replay_is_idempotent() {
        // 6.16.8.24: replaying the same logical operation reuses the same task.
        A2AInvocationIntent intent = A2AInvocationIntent.keyed(
                "exec-1", "op-42", "task-x", "http://remote/agent", "idem-key-1");

        DurableA2ATask first = ledger.createFromIntent(intent, "ckpt-1");
        DurableA2ATask replay = ledger.createFromIntent(intent, "ckpt-2");

        assertSame(first, replay);
        assertEquals(1, ledger.taskCount(), "keyed replay must not create another task");
        assertEquals("ckpt-1", replay.checkpointId(), "original checkpoint anchor is preserved");
    }

    @Test
    void reconciliation_reconstructs_intent_with_same_operation_and_key_after_restart() {
        // Crash → restart: the reconciler must still see the SAME OperationId and
        // IdempotencyKey, reconstructed purely from durable state.
        A2AInvocationIntent intent = A2AInvocationIntent.keyed(
                "exec-2", "op-payment", "task-pay", "http://remote/agent", "idem-pay-1");
        ledger.createFromIntent(intent, "ckpt-1");
        remote.remoteState.put("task-pay", A2ATaskStatus.RUNNING);

        List<A2AInvocationIntent> captured = new ArrayList<>();
        serviceWith(capturing(captured)).reconcileTask("task-pay").toCompletableFuture().join();

        assertEquals(1, captured.size());
        A2AInvocationIntent reconstructed = captured.get(0);
        assertEquals("exec-2", reconstructed.executionId());
        assertEquals("op-payment", reconstructed.operationId(), "OperationId is stable across restart");
        assertEquals("task-pay", reconstructed.taskId());
        assertEquals("idem-pay-1", reconstructed.idempotencyKey().orElseThrow(),
                "Keyed replay must preserve the IdempotencyKey");
    }

    @Test
    void unknown_outcome_with_legacy_wayang_keys_is_still_reconstructed() {
        // Tasks persisted before the wayang to andalus rename keep their legacy keys.
        ledger.createFromIntent(
                A2AInvocationIntent.keyed("exec-3", "op-legacy", "task-legacy",
                        "http://remote/agent", "idem-legacy"), null);
        DurableA2ATask task = ledger.find("task-legacy").orElseThrow();
        // simulate a pre-rename record: only legacy keys present
        // (fresh write with legacy keys merged over the andalus ones)
        // The reconciler prefers andalus.* but must fall back to wayang.*.
        remote.remoteState.put("task-legacy", A2ATaskStatus.RUNNING);

        List<A2AInvocationIntent> captured = new ArrayList<>();
        serviceWith(capturing(captured)).reconcileTask("task-legacy").toCompletableFuture().join();

        assertEquals("op-legacy", captured.get(0).operationId());
        assertEquals("idem-legacy", captured.get(0).idempotencyKey().orElseThrow());
    }

    @Test
    void nonIdempotent_unknown_outcome_is_never_blindly_replayed() {
        // 6.16.8.25: UNKNOWN outcome + no idempotency key: reconciliation resolves
        // nothing and no resubmission happens; the decision belongs to Claw policy.
        A2AInvocationIntent intent = A2AInvocationIntent.withoutKey(
                "exec-4", "op-charge-card", "task-charge", "http://remote/agent");
        ledger.createFromIntent(intent, "ckpt-1");
        remote.networkErrors.add("task-charge");

        DefaultA2AReconciliationService service = serviceWith(new A2AClientTaskReconciler(remote));

        A2ARecoveryDecision decision = service.reconcileTask("task-charge").toCompletableFuture().join();

        assertEquals(A2ARecoveryDecision.UNKNOWN, decision);
        assertTrue(remote.sentMessages.isEmpty(), "non-idempotent unknown must never blind-replay");
        assertEquals(A2ATaskStatus.PENDING, ledger.find("task-charge").orElseThrow().status());
    }

    @Test
    void completion_of_intent_created_task_merges_result_metadata_without_losing_identity() {
        A2AInvocationIntent intent = A2AInvocationIntent.keyed(
                "exec-5", "op-9", "task-9", "http://remote/agent", "idem-9");
        ledger.createFromIntent(intent, null);

        ledger.complete("task-9", List.of(), Map.of("remote.outcome", "ok"));

        DurableA2ATask done = ledger.find("task-9").orElseThrow();
        assertEquals(A2ATaskStatus.COMPLETED, done.status());
        assertEquals("ok", done.metadata().get("remote.outcome"));
        assertEquals("op-9", done.metadata().get(A2AInvocationIntent.META_OPERATION_ID),
                "result envelope merge must not drop the invocation identity");
    }
}
