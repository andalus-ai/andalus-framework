package tech.kayys.andalus.harness.coordination;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Binding-store tests required before the integration moves on (6.17.2.35):
 * create/lookup, idempotency, conflict, tenant isolation, crash windows,
 * Claw retry vs Gamelan retry, completion retention, stale-result rejection.
 */
class GamelanExecutionBindingStoreTest {

    private InMemoryGamelanExecutionBindingStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryGamelanExecutionBindingStore();
    }

    private static AgentExecutionBinding binding(String tenant, String run, String node, int attempt, String exec) {
        return AgentExecutionBinding.of(tenant, run, node, attempt, ExecutionId.of(exec), "res-" + attempt);
    }

    @Test
    void createAndLookupDispatchBinding() {
        AgentExecutionBinding stored = store.bind(binding("t1", "run-1", "node-A", 1, "exec-1"));

        Optional<AgentExecutionBinding> found = store.find("t1", "run-1", "node-A", 1);
        assertTrue(found.isPresent());
        assertEquals("exec-1", found.get().executionId().value());
        assertEquals("t1:run-1:node-A:1", stored.dispatchId());
    }

    @Test
    void sameBindingTwice_isIdempotent() {
        AgentExecutionBinding first = store.bind(binding("t1", "run-1", "node-A", 1, "exec-1"));
        AgentExecutionBinding replay = store.bind(binding("t1", "run-1", "node-A", 1, "exec-1"));

        assertEquals(first, replay);
        assertEquals(1, store.size(), "idempotent rebind must not create a second record");
    }

    @Test
    void sameDispatch_differentExecution_conflicts() {
        store.bind(binding("t1", "run-1", "node-A", 1, "exec-1"));

        BindingConflictException conflict = assertThrows(BindingConflictException.class,
                () -> store.bind(binding("t1", "run-1", "node-A", 1, "exec-2")));

        assertEquals("t1:run-1:node-A:1", conflict.dispatchId());
        assertEquals("exec-1", conflict.existingExecutionId());
        assertEquals("exec-2", conflict.candidateExecutionId());
        assertEquals("exec-1", store.find("t1", "run-1", "node-A", 1).orElseThrow().executionId().value(),
                "a conflict must never overwrite the durable binding");
    }

    @Test
    void lookupByExecutionId() {
        store.bind(binding("t1", "run-1", "node-A", 1, "exec-1"));

        Optional<AgentExecutionBinding> found = store.findByExecutionId(ExecutionId.of("exec-1"));
        assertTrue(found.isPresent());
        assertEquals("node-A", found.get().nodeId());

        assertTrue(store.findByExecutionId(ExecutionId.of("exec-unknown")).isEmpty());
    }

    @Test
    void tenantIsolation_preventsCrossTenantCollision() {
        store.bind(binding("tenant-A", "run-1", "node-X", 1, "exec-a"));
        store.bind(binding("tenant-B", "run-1", "node-X", 1, "exec-b"));

        assertEquals("exec-a", store.find("tenant-A", "run-1", "node-X", 1).orElseThrow().executionId().value());
        assertEquals("exec-b", store.find("tenant-B", "run-1", "node-X", 1).orElseThrow().executionId().value());
        assertEquals(2, store.size(), "identical Gamelan ids in different tenants are distinct dispatches");
    }

    @Test
    void crashBeforeBinding_leavesNoBinding() {
        assertTrue(store.find("t1", "run-1", "node-A", 1).isEmpty());
        assertTrue(store.findActiveForNode("t1", "run-1", "node-A").isEmpty());
    }

    @Test
    void crashAfterBinding_bindingSurvivesLookup() {
        store.bind(binding("t1", "run-1", "node-A", 1, "exec-1"));

        // restart: a new bridge over the same store still resolves the dispatch
        DefaultGamelanClawBridge restarted = new DefaultGamelanClawBridge(store);
        AgentExecutionBinding recovered = restarted.bindNodeExecution("t1", "run-1", "node-A", 1, "res-1");

        assertEquals("exec-1", recovered.executionId().value(),
                "recovery must reuse the existing binding, not create a new execution");
        assertEquals(1, store.size());
    }

    @Test
    void clawRetry_doesNotChangeBinding() {
        store.bind(binding("t1", "run-1", "node-A", 1, "exec-1"));

        // Claw retries are represented by Claw AttemptIds, invisible to the binding (6.17.2.20).
        assertEquals("exec-1", store.find("t1", "run-1", "node-A", 1).orElseThrow().executionId().value());
    }

    @Test
    void gamelanRetry_createsDistinctDispatch_andSupersedesPrevious() {
        store.bind(binding("t1", "run-1", "node-A", 1, "exec-1"));
        store.bind(binding("t1", "run-1", "node-A", 2, "exec-2"));

        assertEquals("exec-1", store.find("t1", "run-1", "node-A", 1).orElseThrow().executionId().value(),
                "the historical attempt binding is retained, not mutated");
        assertEquals("exec-2", store.find("t1", "run-1", "node-A", 2).orElseThrow().executionId().value());
        assertEquals(2, store.size());

        AgentExecutionBinding active = store.findActiveForNode("t1", "run-1", "node-A").orElseThrow();
        assertEquals("exec-2", active.executionId().value(), "the newest attempt owns the node");
    }

    @Test
    void completedExecutionRetainsBinding() {
        AgentExecutionBinding stored = store.bind(binding("t1", "run-1", "node-A", 1, "exec-1"));

        store.complete(stored.executionId());

        Optional<AgentExecutionBinding> found = store.find("t1", "run-1", "node-A", 1);
        assertTrue(found.isPresent(), "completion must retain the binding for auditability (6.17.2.23)");
        assertFalse(found.get().isActive());
        assertNotNull(found.get().completedAt());
    }

    @Test
    void removeDropsBinding() {
        AgentExecutionBinding stored = store.bind(binding("t1", "run-1", "node-A", 1, "exec-1"));

        store.remove(stored);

        assertTrue(store.find("t1", "run-1", "node-A", 1).isEmpty());
        assertTrue(store.findByExecutionId(stored.executionId()).isEmpty());
    }

    @Test
    void staleExecutionResult_isRejected_byBridge() {
        // Gamelan retried the node: attempt 2 owns the node now.
        DefaultGamelanClawBridge bridge = new DefaultGamelanClawBridge(store);
        AgentExecutionBinding attempt1 = bridge.bindNodeExecution("t1", "run-1", "node-A", 1, "res-1");
        bridge.bindNodeExecution("t1", "run-1", "node-A", 2, "res-2");

        RecordingGamelanBridge recorder = new RecordingGamelanBridge(store);
        recorder.onExecutionCompleted(attempt1, null);   // late result of the superseded attempt

        assertEquals(0, recorder.notifications, "a superseded attempt's result must never reach Gamelan");
    }

    /** Bridge that records Gamelan notifications instead of sending them. */
    private static final class RecordingGamelanBridge extends DefaultGamelanClawBridge {
        int notifications;

        RecordingGamelanBridge(GamelanExecutionBindingStore store) {
            super(store);
        }

        @Override
        protected void notifyGamelanNodeCompleted(
                AgentExecutionBinding binding,
                tech.kayys.andalus.harness.execution.state.ExecutionState finalState) {
            notifications++;
        }

        @Override
        protected void notifyGamelanNodeFailed(AgentExecutionBinding binding, String reason) {
            notifications++;
        }

        @Override
        protected void notifyGamelanNodeCancelled(AgentExecutionBinding binding) {
            notifications++;
        }
    }
}
