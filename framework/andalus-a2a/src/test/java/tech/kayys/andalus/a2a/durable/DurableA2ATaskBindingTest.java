package tech.kayys.andalus.a2a.durable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2a.model.A2ATaskStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the 6.16.3 task-binding semantics: canonical identity model,
 * remote execution binding, and caller-scoped lookup.
 */
class DurableA2ATaskBindingTest {

    private DurableA2ATaskLedger ledger;

    @BeforeEach
    void setUp() {
        ledger = new DurableA2ATaskLedger();
    }

    @Test
    void createForCaller_leaves_remote_execution_unbound() {
        DurableA2ATask task = ledger.createForCaller("exec-local-1", "http://remote/agent", "ckpt-1");

        assertNotNull(task.taskId());
        assertEquals("exec-local-1", task.callerExecutionId());
        assertNull(task.executionId(), "remote execution id must not be initialized to the caller id");
        assertEquals(A2ATaskStatus.PENDING, task.status());
    }

    @Test
    void createForCaller_rejects_blank_identities() {
        assertThrows(IllegalArgumentException.class,
                () -> DurableA2ATask.createForCaller(" ", "exec-1", "http://remote", null));
        assertThrows(IllegalArgumentException.class,
                () -> DurableA2ATask.createForCaller("task-1", "", "http://remote", null));
        assertThrows(IllegalArgumentException.class,
                () -> DurableA2ATask.createForCaller("task-1", "exec-1", " ", null));
    }

    @Test
    void bindRemoteExecution_binds_discovered_remote_execution() {
        DurableA2ATask task = ledger.createForCaller("exec-local-2", "http://remote/agent", null);

        DurableA2ATask bound = ledger.bindRemoteExecution(task.taskId(), "exec-remote-999");

        assertEquals("exec-remote-999", bound.executionId());
        assertEquals("exec-local-2", bound.callerExecutionId());
    }

    @Test
    void bindRemoteExecution_is_idempotent_for_same_value() {
        DurableA2ATask task = ledger.createForCaller("exec-local-3", "http://remote/agent", null);

        ledger.bindRemoteExecution(task.taskId(), "exec-remote-999");
        DurableA2ATask rebound = ledger.bindRemoteExecution(task.taskId(), "exec-remote-999");

        assertEquals("exec-remote-999", rebound.executionId());
    }

    @Test
    void bindRemoteExecution_rejects_conflicting_binding() {
        DurableA2ATask task = ledger.createForCaller("exec-local-4", "http://remote/agent", null);
        ledger.bindRemoteExecution(task.taskId(), "exec-remote-999");

        assertThrows(IllegalStateException.class,
                () -> ledger.bindRemoteExecution(task.taskId(), "exec-remote-888"));
    }

    @Test
    void bindRemoteExecution_rejects_unknown_task() {
        assertThrows(IllegalArgumentException.class,
                () -> ledger.bindRemoteExecution("no-such-task", "exec-remote-1"));
    }

    @Test
    void findForCallerExecution_scopes_lookup_to_owner() {
        DurableA2ATask task = ledger.createForCaller("exec-local-5", "http://remote/agent", null);

        Optional<DurableA2ATask> found = ledger.findForCallerExecution("exec-local-5", task.taskId());
        assertTrue(found.isPresent());

        assertTrue(ledger.findForCallerExecution("exec-other", task.taskId()).isEmpty());
        assertTrue(ledger.findForCallerExecution(null, task.taskId()).isEmpty());
        assertTrue(ledger.findForCallerExecution("exec-local-5", null).isEmpty());
    }

    @Test
    void complete_with_result_persists_envelope_before_terminal() {
        DurableA2ATask task = ledger.createForCaller("exec-local-6", "http://remote/agent", null);

        DurableA2ATask done = ledger.complete(task.taskId(),
                java.util.List.of(),
                java.util.Map.of("andalus.operation.id", "op-1"));

        assertEquals(A2ATaskStatus.COMPLETED, done.status());
        assertEquals("op-1", done.metadata().get("andalus.operation.id"));
        assertTrue(done.isTerminal());
    }
}
