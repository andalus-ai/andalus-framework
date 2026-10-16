package tech.kayys.andalus.a2a.durable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2a.model.A2AArtifact;
import tech.kayys.andalus.a2a.model.A2ATaskStatus;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the 6.16.5 completion bridge: persist-before-wake ordering,
 * result envelope, and duplicate-terminal idempotency.
 */
class DefaultA2ACompletionBridgeTest {

    private DurableA2ATaskLedger ledger;
    private List<String> wakes;
    private DefaultA2ACompletionBridge bridge;

    @BeforeEach
    void setUp() {
        ledger = new DurableA2ATaskLedger();
        wakes = new ArrayList<>();
        A2AWakeCoordinator wakeCoordinator = new DefaultA2AWakeCoordinator(ledger, wakes::add);
        bridge = new DefaultA2ACompletionBridge(ledger, wakeCoordinator);
    }

    @Test
    void completed_persists_terminal_state_before_wake() {
        DurableA2ATask task = ledger.createForCaller("exec-1", "http://remote/agent", null);

        bridge.completed(task.taskId());

        DurableA2ATask persisted = ledger.find(task.taskId()).orElseThrow();
        assertEquals(A2ATaskStatus.COMPLETED, persisted.status());
        assertNotNull(persisted.completedAt());
        assertEquals(List.of("exec-1"), wakes);
    }

    @Test
    void completed_persists_result_envelope() {
        DurableA2ATask task = ledger.createForCaller("exec-2", "http://remote/agent", null);
        A2AArtifact artifact = new A2AArtifact(
                "art-1", "report.pdf", "application/pdf", 42L,
                URI.create("https://remote/artifacts/art-1"), Map.of());

        bridge.completed(task.taskId(), List.of(artifact), Map.of("remote.outcome", "ok"));

        DurableA2ATask persisted = ledger.find(task.taskId()).orElseThrow();
        assertEquals(1, persisted.artifacts().size());
        assertEquals("art-1", persisted.artifacts().get(0).id());
        assertEquals("ok", persisted.metadata().get("remote.outcome"));
    }

    @Test
    void duplicate_completion_does_not_reatransition_but_wake_is_safe() {
        DurableA2ATask task = ledger.createForCaller("exec-3", "http://remote/agent", null);

        bridge.completed(task.taskId());
        bridge.completed(task.taskId());

        DurableA2ATask persisted = ledger.find(task.taskId()).orElseThrow();
        assertEquals(A2ATaskStatus.COMPLETED, persisted.status());
        // Duplicate wake is harmless — the wake path revalidates durable state.
        assertEquals(List.of("exec-3", "exec-3"), wakes);
    }

    @Test
    void failed_persists_terminal_and_wakes() {
        DurableA2ATask task = ledger.createForCaller("exec-4", "http://remote/agent", null);

        bridge.failed(task.taskId(), "boom");

        assertEquals(A2ATaskStatus.FAILED, ledger.find(task.taskId()).orElseThrow().status());
        assertEquals(List.of("exec-4"), wakes);
    }

    @Test
    void cancelled_persists_terminal_and_wakes() {
        DurableA2ATask task = ledger.createForCaller("exec-5", "http://remote/agent", null);

        bridge.cancelled(task.taskId());

        assertEquals(A2ATaskStatus.CANCELLED, ledger.find(task.taskId()).orElseThrow().status());
        assertEquals(List.of("exec-5"), wakes);
    }

    @Test
    void terminal_task_is_not_overwritten_by_later_signal() {
        DurableA2ATask task = ledger.createForCaller("exec-6", "http://remote/agent", null);

        bridge.completed(task.taskId());
        bridge.failed(task.taskId(), "late failure");

        assertEquals(A2ATaskStatus.COMPLETED, ledger.find(task.taskId()).orElseThrow().status());
    }

    @Test
    void unknown_task_rejected() {
        assertThrows(IllegalArgumentException.class, () -> bridge.completed("no-such-task"));
        assertThrows(IllegalArgumentException.class, () -> bridge.failed("no-such-task", "x"));
        assertThrows(IllegalArgumentException.class, () -> bridge.cancelled("no-such-task"));
    }
}
