package tech.kayys.wayang.harness.journal;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.consistency.state.ExecutionId;
import tech.kayys.wayang.harness.workflow.GraphState;
import tech.kayys.wayang.harness.workflow.NodeId;
import tech.kayys.wayang.harness.workflow.NodeState;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JournalTest {

    @Test
    void testJournalAppendAndRead() {
        ExecutionJournal journal = new InMemoryExecutionJournal();
        ExecutionId executionId = ExecutionId.of("exec-journal-1");

        ExecutionEvent event1 = DefaultExecutionEvent.of(
                executionId,
                EventSequence.of(1),
                ExecutionEventType.GRAPH_STARTED
        );

        ExecutionEvent event2 = DefaultExecutionEvent.ofNode(
                executionId,
                EventSequence.of(2),
                ExecutionEventType.NODE_STARTED,
                NodeId.of("node-1")
        );

        journal.append(event1);
        journal.append(event2);

        List<ExecutionEvent> events = journal.read(executionId, JournalPosition.beginning());
        assertEquals(2, events.size());
        assertEquals(ExecutionEventType.GRAPH_STARTED, events.get(0).type());
        assertEquals(ExecutionEventType.NODE_STARTED, events.get(1).type());

        JournalPosition pos = journal.position(executionId);
        assertEquals(2, pos.sequence());
    }

    @Test
    void testStateProjectorAndRecoveryEngine() {
        ExecutionJournal journal = new InMemoryExecutionJournal();
        SnapshotStore snapshotStore = new InMemorySnapshotStore();
        ExecutionStateProjector projector = new DefaultExecutionStateProjector();
        ExecutionRecoveryEngine recoveryEngine = new DefaultExecutionRecoveryEngine(journal, snapshotStore, projector);

        ExecutionId executionId = ExecutionId.of("exec-recover-1");
        NodeId nodeA = NodeId.of("node-A");

        // Append initial events
        journal.append(DefaultExecutionEvent.of(
                executionId,
                EventSequence.of(1),
                ExecutionEventType.GRAPH_STARTED
        ));

        journal.append(DefaultExecutionEvent.ofNode(
                executionId,
                EventSequence.of(2),
                ExecutionEventType.NODE_STARTED,
                nodeA
        ));

        // Project state after event 2
        List<ExecutionEvent> events = journal.read(executionId, JournalPosition.beginning());
        Map<NodeId, NodeState> nodeStates = projector.projectNodeStates(events);
        GraphState graphState = projector.projectGraphState(events);
        assertEquals(GraphState.RUNNING, graphState);
        assertEquals(NodeState.RUNNING, nodeStates.get(nodeA));

        // Save snapshot at position 2
        SnapshotPayload snapshotPayload = SnapshotPayload.of(nodeStates, graphState);
        ExecutionSnapshot snapshot = DefaultExecutionSnapshot.of(
                executionId,
                JournalPosition.of(2),
                snapshotPayload
        );
        snapshotStore.save(snapshot);

        // Append event at seq 3: node completed
        journal.append(DefaultExecutionEvent.ofNode(
                executionId,
                EventSequence.of(3),
                ExecutionEventType.NODE_COMPLETED,
                nodeA
        ));

        // Recover state: should load snapshot at pos 2, replay event 3, and reach node completed
        SnapshotPayload recovered = recoveryEngine.recover(executionId);
        assertNotNull(recovered);
        assertEquals(NodeState.COMPLETED, recovered.nodeStates().get(nodeA));
    }
}
