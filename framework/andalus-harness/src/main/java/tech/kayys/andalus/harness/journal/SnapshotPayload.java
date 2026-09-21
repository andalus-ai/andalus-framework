package tech.kayys.andalus.harness.journal;

import tech.kayys.andalus.workflow.graph.GraphState;
import tech.kayys.andalus.workflow.graph.NodeId;
import tech.kayys.andalus.workflow.graph.NodeState;

import java.util.Map;

/**
 * State payload captured within a point-in-time snapshot.
 */
public record SnapshotPayload(
        Map<NodeId, NodeState> nodeStates,
        GraphState graphState,
        Map<String, Object> attributes
) {

    public SnapshotPayload {
        nodeStates = nodeStates != null ? Map.copyOf(nodeStates) : Map.of();
        graphState = graphState != null ? graphState : GraphState.DRAFT;
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }

    public static SnapshotPayload of(Map<NodeId, NodeState> nodeStates, GraphState graphState) {
        return new SnapshotPayload(nodeStates, graphState, Map.of());
    }

    public static SnapshotPayload empty() {
        return new SnapshotPayload(Map.of(), GraphState.DRAFT, Map.of());
    }
}
