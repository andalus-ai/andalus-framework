package tech.kayys.andalus.harness.execution.lifecycle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.context.DefaultHarnessContext;
import tech.kayys.andalus.harness.context.DefaultHarnessIdentity;
import tech.kayys.andalus.harness.context.DefaultHarnessSession;
import tech.kayys.andalus.harness.execution.state.DefaultExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionCursor;
import tech.kayys.andalus.harness.execution.state.ExecutionData;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionStatus;
import tech.kayys.andalus.harness.execution.state.InMemoryExecutionStateStore;
import tech.kayys.andalus.harness.lifecycle.DefaultHarnessLifecycle;
import tech.kayys.andalus.harness.lifecycle.HarnessExecutionStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ExecutionLifecycleCoordinator (6.15.3).
 * Verifies the durable-first transition order and legal-transition guard.
 */
class ExecutionLifecycleCoordinatorTest {

    private InMemoryExecutionStateStore stateStore;
    private DefaultExecutionLifecycleCoordinator coordinator;
    private ExecutionId executionId;

    @BeforeEach
    void setUp() {
        stateStore  = new InMemoryExecutionStateStore();
        executionId = ExecutionId.of("exec-lifecycle-001");

        DefaultHarnessContext ctx = new DefaultHarnessContext(
                DefaultHarnessIdentity.of("agent-lc"),
                DefaultHarnessSession.createNew(),
                Map.of());

        DefaultExecutionState initial = new DefaultExecutionState(
                executionId, ExecutionStatus.RUNNING, 0L, ctx,
                ExecutionCursor.initial(), ExecutionData.empty());
        stateStore.save(initial, -1L); // persist initial state

        coordinator = new DefaultExecutionLifecycleCoordinator(
                initial, stateStore, new DefaultHarnessLifecycle(HarnessExecutionStatus.RUNNING));
    }

    @Test
    void initialState_isRunning() {
        assertEquals(ExecutionStatus.RUNNING, coordinator.state().status());
        assertEquals(HarnessExecutionStatus.RUNNING, coordinator.lifecycleStatus());
        assertFalse(coordinator.isTerminal());
    }

    @Test
    void transitionRunningToWaiting_persistsDurableFirst() {
        coordinator.transitionTo(HarnessExecutionStatus.WAITING);

        // Durable state is updated
        assertEquals(ExecutionStatus.WAITING, coordinator.state().status());
        // In-process lifecycle is updated
        assertEquals(HarnessExecutionStatus.WAITING, coordinator.lifecycleStatus());
        // StateStore holds new durable record
        assertTrue(stateStore.load(executionId).isPresent());
        assertEquals(ExecutionStatus.WAITING, stateStore.load(executionId).get().status());
    }

    @Test
    void transitionRunningToCompleted_marksTerminal() {
        coordinator.transitionTo(HarnessExecutionStatus.COMPLETED);

        assertEquals(ExecutionStatus.COMPLETED, coordinator.state().status());
        assertTrue(coordinator.isTerminal());
    }

    @Test
    void transitionFromTerminal_throws() {
        coordinator.transitionTo(HarnessExecutionStatus.COMPLETED);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> coordinator.transitionTo(HarnessExecutionStatus.RUNNING));
        assertTrue(ex.getMessage().contains("terminal"));
    }

    @Test
    void idempotentTransition_isNoOp() {
        // RUNNING → RUNNING should not throw or change version
        long versionBefore = coordinator.state().version();
        coordinator.transitionTo(HarnessExecutionStatus.RUNNING);
        assertEquals(versionBefore, coordinator.state().version());
    }

    @Test
    void illegalDurableTransition_throws() {
        // WAITING → COMPLETED is not allowed directly in durable model
        coordinator.transitionTo(HarnessExecutionStatus.WAITING);

        // WAITING → COMPLETED is not in the allowed set → should throw
        assertThrows(IllegalStateException.class,
                () -> coordinator.transitionTo(HarnessExecutionStatus.COMPLETED));
    }

    @Test
    void statusMapper_roundtrip_running() {
        assertEquals(ExecutionStatus.RUNNING,
                ExecutionStatusMapper.durable(HarnessExecutionStatus.RUNNING));
        assertEquals(HarnessExecutionStatus.RUNNING,
                ExecutionStatusMapper.harness(ExecutionStatus.RUNNING));
    }

    @Test
    void statusMapper_expiredMapsToTimeout() {
        assertEquals(ExecutionStatus.TIMEOUT,
                ExecutionStatusMapper.durable(HarnessExecutionStatus.EXPIRED));
        assertEquals(HarnessExecutionStatus.EXPIRED,
                ExecutionStatusMapper.harness(ExecutionStatus.TIMEOUT));
    }

    @Test
    void statusMapper_admittedAndInitializingMapToRunning() {
        assertEquals(ExecutionStatus.RUNNING,
                ExecutionStatusMapper.durable(HarnessExecutionStatus.ADMITTED));
        assertEquals(ExecutionStatus.RUNNING,
                ExecutionStatusMapper.durable(HarnessExecutionStatus.INITIALIZING));
    }
}
