package tech.kayys.andalus.a2a.durable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the 6.16.4 wake adapter: registration verification, transient
 * registration with durable-ledger fallback, and caller-scoped wakes.
 */
class DefaultA2AWakeCoordinatorTest {

    private DurableA2ATaskLedger ledger;
    private List<String> wakes;
    private DefaultA2AWakeCoordinator coordinator;

    @BeforeEach
    void setUp() {
        ledger = new DurableA2ATaskLedger();
        wakes = new ArrayList<>();
        coordinator = new DefaultA2AWakeCoordinator(ledger, wakes::add);
    }

    @Test
    void register_verifies_task_and_caller() {
        DurableA2ATask task = ledger.createForCaller("exec-local-1", "http://remote/agent", null);

        coordinator.register("exec-local-1", task.taskId());

        coordinator.complete(task.taskId());
        assertEquals(List.of("exec-local-1"), wakes);
    }

    @Test
    void register_rejects_unknown_task() {
        assertThrows(IllegalArgumentException.class,
                () -> coordinator.register("exec-local-1", "no-such-task"));
    }

    @Test
    void register_rejects_wrong_caller() {
        DurableA2ATask task = ledger.createForCaller("exec-local-1", "http://remote/agent", null);

        assertThrows(IllegalArgumentException.class,
                () -> coordinator.register("exec-other", task.taskId()));
    }

    @Test
    void complete_falls_back_to_ledger_when_registration_lost() {
        // Simulates restart: no transient registration, durable ledger intact.
        DurableA2ATask task = ledger.createForCaller("exec-local-2", "http://remote/agent", null);

        coordinator.complete(task.taskId());

        assertEquals(List.of("exec-local-2"), wakes);
    }

    @Test
    void fail_and_cancel_wake_the_caller() {
        DurableA2ATask t1 = ledger.createForCaller("exec-local-3", "http://remote/agent", null);
        DurableA2ATask t2 = ledger.createForCaller("exec-local-4", "http://remote/agent", null);

        coordinator.fail(t1.taskId());
        coordinator.cancel(t2.taskId());

        assertEquals(List.of("exec-local-3", "exec-local-4"), wakes);
    }

    @Test
    void wake_for_unknown_task_is_ignored() {
        coordinator.complete("no-such-task");
        assertTrue(wakes.isEmpty());
    }
}
