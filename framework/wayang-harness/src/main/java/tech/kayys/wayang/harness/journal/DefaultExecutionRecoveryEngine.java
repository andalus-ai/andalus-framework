package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;
import tech.kayys.wayang.harness.workflow.GraphState;
import tech.kayys.wayang.harness.workflow.NodeId;
import tech.kayys.wayang.harness.workflow.NodeState;

import java.util.*;

/**
 * Standard implementation of {@link ExecutionRecoveryEngine}.
 */
public class DefaultExecutionRecoveryEngine implements ExecutionRecoveryEngine {

    private final ExecutionJournal journal;
    private final SnapshotStore snapshotStore;
    private final ExecutionStateProjector projector;

    public DefaultExecutionRecoveryEngine(
            ExecutionJournal journal,
            SnapshotStore snapshotStore,
            ExecutionStateProjector projector
    ) {
        this.journal = Objects.requireNonNull(journal, "ExecutionJournal cannot be null");
        this.snapshotStore = Objects.requireNonNull(snapshotStore, "SnapshotStore cannot be null");
        this.projector = Objects.requireNonNull(projector, "ExecutionStateProjector cannot be null");
    }

    @Override
    public SnapshotPayload recover(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");

        Optional<ExecutionSnapshot> latestSnapshot = snapshotStore.latest(executionId);
        Map<NodeId, NodeState> recoveredNodeStates = new HashMap<>();
        GraphState recoveredGraphState = GraphState.DRAFT;

        List<ExecutionEvent> eventsToReplay;
        if (latestSnapshot.isPresent()) {
            ExecutionSnapshot snap = latestSnapshot.get();
            recoveredNodeStates.putAll(snap.payload().nodeStates());
            recoveredGraphState = snap.payload().graphState();

            // Read events that occurred strictly after this snapshot position
            eventsToReplay = journal.read(executionId, snap.position().next());
        } else {
            // Replay from the beginning of history
            eventsToReplay = journal.history(executionId);
        }

        if (!eventsToReplay.isEmpty()) {
            Map<NodeId, NodeState> tailNodeStates = projector.projectNodeStates(eventsToReplay);
            recoveredNodeStates.putAll(tailNodeStates);
            recoveredGraphState = projector.projectGraphState(eventsToReplay);
        }

        return SnapshotPayload.of(recoveredNodeStates, recoveredGraphState);
    }
}
