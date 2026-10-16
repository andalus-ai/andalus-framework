package tech.kayys.andalus.harness.coordination;

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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Startup-adapter tests (6.17.3.11–6.17.3.16): given what Gamelan still considers
 * active, the reconciler must decide the right synchronization action without
 * touching either engine's state.
 */
class GamelanClawStartupReconcilerTest {

    private InMemoryGamelanExecutionBindingStore bindings;
    private InMemoryExecutionStateStore states;
    private DefaultGamelanClawBridge bridge;
    private GamelanClawStartupReconciler reconciler;

    @BeforeEach
    void setUp() {
        bindings = new InMemoryGamelanExecutionBindingStore();
        states = new InMemoryExecutionStateStore();
        bridge = new DefaultGamelanClawBridge(bindings);
        reconciler = new GamelanClawStartupReconciler(bindings, states);
    }

    private static GamelanDispatchDescriptor dispatch(String run, String node, int attempt) {
        return new GamelanDispatchDescriptor("tenant-1", run, node, attempt, "res-" + attempt);
    }

    private void saveState(ExecutionId id, ExecutionStatus status) {
        var ctx = new DefaultHarnessContext(
                DefaultHarnessIdentity.of("agent-1"),
                DefaultHarnessSession.createNew(),
                Map.of());
        states.save(new DefaultExecutionState(id, status, 0L, ctx,
                ExecutionCursor.initial(), ExecutionData.empty()), -1L);
    }

    @Test
    void case1_bindingAndLiveExecution_keepWaiting() {
        AgentExecutionBinding binding = bridge.bindNodeExecution("tenant-1", "run-1", "node-A", 1, "res-1");
        saveState(binding.executionId(), ExecutionStatus.WAITING);

        GamelanClawReconciliation result = reconciler.reconcileOne(dispatch("run-1", "node-A", 1));

        assertEquals(GamelanReconciliationAction.KEEP_WAITING, result.action());
        assertEquals(ExecutionStatus.WAITING, result.executionStatus().orElseThrow());
        assertFalse(result.requiresGamelanNotification());
    }

    @Test
    void case1_bindingAndCompletedExecution_notifyCompleted() {
        AgentExecutionBinding binding = bridge.bindNodeExecution("tenant-1", "run-1", "node-A", 1, "res-1");
        saveState(binding.executionId(), ExecutionStatus.COMPLETED);

        GamelanClawReconciliation result = reconciler.reconcileOne(dispatch("run-1", "node-A", 1));

        assertEquals(GamelanReconciliationAction.NOTIFY_COMPLETED, result.action());
        assertTrue(result.requiresGamelanNotification(),
                "a terminal Claw execution whose notification was lost must be re-reported");
    }

    @Test
    void case1_bindingAndFailedOrCancelledExecution_notifyFailureOrCancellation() {
        AgentExecutionBinding failed = bridge.bindNodeExecution("tenant-1", "run-1", "node-F", 1, "res-f");
        saveState(failed.executionId(), ExecutionStatus.FAILED);
        assertEquals(GamelanReconciliationAction.NOTIFY_FAILED,
                reconciler.reconcileOne(dispatch("run-1", "node-F", 1)).action());

        AgentExecutionBinding cancelled = bridge.bindNodeExecution("tenant-1", "run-1", "node-C", 1, "res-c");
        saveState(cancelled.executionId(), ExecutionStatus.CANCELED);
        assertEquals(GamelanReconciliationAction.NOTIFY_CANCELLED,
                reconciler.reconcileOne(dispatch("run-1", "node-C", 1)).action());
    }

    @Test
    void case2_bindingButMissingExecution_markLost() {
        bridge.bindNodeExecution("tenant-1", "run-2", "node-B", 1, "res-1");
        // the Claw worker that owned the execution disappeared

        GamelanClawReconciliation result = reconciler.reconcileOne(dispatch("run-2", "node-B", 1));

        assertEquals(GamelanReconciliationAction.MARK_LOST, result.action());
        assertTrue(result.executionStatus().isEmpty());
        assertTrue(result.requiresGamelanNotification(),
                "Gamelan must learn the node lost its worker so it can re-dispatch or fail it");
    }

    @Test
    void case3_noBinding_requiresRebind() {
        GamelanClawReconciliation result = reconciler.reconcileOne(dispatch("run-3", "node-D", 1));

        assertEquals(GamelanReconciliationAction.REBIND_REQUIRED, result.action());
        assertTrue(result.binding().isEmpty());
    }

    @Test
    void case5_supersededAttempt_isStale() {
        bridge.bindNodeExecution("tenant-1", "run-5", "node-E", 1, "res-1");
        AgentExecutionBinding current = bridge.bindNodeExecution("tenant-1", "run-5", "node-E", 2, "res-2");
        saveState(current.executionId(), ExecutionStatus.RUNNING);

        GamelanClawReconciliation historical = reconciler.reconcileOne(dispatch("run-5", "node-E", 1));
        GamelanClawReconciliation latest = reconciler.reconcileOne(dispatch("run-5", "node-E", 2));

        assertEquals(GamelanReconciliationAction.STALE_DISPATCH, historical.action(),
                "the historical attempt must not be treated as the node's current dispatch");
        assertEquals(GamelanReconciliationAction.KEEP_WAITING, latest.action());
    }

    @Test
    void reconcile_isIdempotent_andCoversAllDispatches() {
        AgentExecutionBinding binding = bridge.bindNodeExecution("tenant-1", "run-6", "node-G", 1, "res-1");
        saveState(binding.executionId(), ExecutionStatus.COMPLETED);
        List<GamelanDispatchDescriptor> dispatches = List.of(
                dispatch("run-6", "node-G", 1),
                dispatch("run-6", "node-H", 1));

        List<GamelanClawReconciliation> first = reconciler.reconcile(dispatches);
        List<GamelanClawReconciliation> second = reconciler.reconcile(dispatches);

        assertEquals(2, first.size());
        assertEquals(List.of(GamelanReconciliationAction.NOTIFY_COMPLETED,
                        GamelanReconciliationAction.REBIND_REQUIRED),
                first.stream().map(GamelanClawReconciliation::action).toList());
        assertEquals(first.stream().map(GamelanClawReconciliation::action).toList(),
                second.stream().map(GamelanClawReconciliation::action).toList(),
                "startup reconciliation must be repeatable across instances (6.17.3.26)");
        assertEquals(1, bindings.size(), "reconciliation never creates executions or bindings");
    }
}
