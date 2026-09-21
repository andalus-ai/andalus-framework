package tech.kayys.andalus.harness.journal;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.consistency.state.ExecutionId;
import tech.kayys.andalus.workflow.graph.GraphState;
import tech.kayys.andalus.workflow.graph.NodeId;
import tech.kayys.andalus.workflow.graph.NodeState;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the seeded graph-state projection fix (6.13.7).
 */
class SeededProjectionTest {

    @Test
    void projectGraphState_noSeed_degradesToDraft() {
        DefaultExecutionStateProjector projector = new DefaultExecutionStateProjector();
        GraphState result = projector.projectGraphState(List.of());
        assertEquals(GraphState.DRAFT, result);
    }

    @Test
    void projectGraphState_withSeed_preservesSeedWhenNoEvents() {
        DefaultExecutionStateProjector projector = new DefaultExecutionStateProjector();
        GraphState result = projector.projectGraphState(GraphState.RUNNING, List.of());
        assertEquals(GraphState.RUNNING, result, "Empty tail should preserve checkpoint seed");
    }

    @Test
    void projectGraphState_withSeed_advancesFromSeed() {
        // Simulate: checkpoint is RUNNING, tail has NODE_COMPLETED (no graph-level event)
        DefaultExecutionStateProjector projector = new DefaultExecutionStateProjector();
        ExecutionId id = ExecutionId.of("exec-seed-1");
        NodeId nodeA = NodeId.of("node-A");

        List<ExecutionEvent> tail = List.of(
                DefaultExecutionEvent.ofNode(id, EventSequence.of(3), ExecutionEventType.NODE_COMPLETED, nodeA)
        );

        GraphState result = projector.projectGraphState(GraphState.RUNNING, tail);
        assertEquals(GraphState.RUNNING, result,
                "NODE_COMPLETED should not downgrade RUNNING graph — should stay RUNNING");
    }

    @Test
    void projectGraphState_withSeed_completesGraphFromRunning() {
        DefaultExecutionStateProjector projector = new DefaultExecutionStateProjector();
        ExecutionId id = ExecutionId.of("exec-seed-2");

        List<ExecutionEvent> tail = List.of(
                DefaultExecutionEvent.of(id, EventSequence.of(5), ExecutionEventType.GRAPH_COMPLETED)
        );

        GraphState result = projector.projectGraphState(GraphState.RUNNING, tail);
        assertEquals(GraphState.COMPLETED, result);
    }

    @Test
    void recoveryEngine_usesSeededProjection() {
        // Regression test: tail replay must not regress to DRAFT when snapshot has RUNNING graph state
        ExecutionJournal journal = new InMemoryExecutionJournal();
        SnapshotStore snapshotStore = new InMemorySnapshotStore();
        ExecutionStateProjector projector = new DefaultExecutionStateProjector();
        ExecutionRecoveryEngine engine = new DefaultExecutionRecoveryEngine(journal, snapshotStore, projector);

        ExecutionId id = ExecutionId.of("exec-crash-recover");
        NodeId nodeA = NodeId.of("n-A");

        // Phase 1: initial events — graph starts
        journal.append(DefaultExecutionEvent.of(id, EventSequence.of(1), ExecutionEventType.GRAPH_STARTED));
        journal.append(DefaultExecutionEvent.ofNode(id, EventSequence.of(2), ExecutionEventType.NODE_STARTED, nodeA));

        // Take snapshot at position 2 with RUNNING graph state
        java.util.Map<NodeId, NodeState> snap1NodeStates = java.util.Map.of(nodeA, NodeState.RUNNING);
        SnapshotPayload snap1 = SnapshotPayload.of(snap1NodeStates, GraphState.RUNNING);
        snapshotStore.save(DefaultExecutionSnapshot.of(id, JournalPosition.of(2), snap1));

        // Phase 2: tail — node completes (no GRAPH_STARTED in this tail)
        journal.append(DefaultExecutionEvent.ofNode(id, EventSequence.of(3), ExecutionEventType.NODE_COMPLETED, nodeA));

        // Recover: should produce RUNNING graph (not DRAFT), node COMPLETED
        SnapshotPayload recovered = engine.recover(id);
        assertEquals(GraphState.RUNNING, recovered.graphState(),
                "Recovery must preserve checkpoint graph state RUNNING when tail has no graph transition event");
        assertEquals(NodeState.COMPLETED, recovered.nodeStates().get(nodeA));
    }
}
