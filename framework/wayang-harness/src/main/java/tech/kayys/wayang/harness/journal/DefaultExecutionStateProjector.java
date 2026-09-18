package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.workflow.graph.GraphState;
import tech.kayys.wayang.workflow.graph.NodeId;
import tech.kayys.wayang.workflow.graph.NodeState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Standard implementation of {@link ExecutionStateProjector}.
 */
public class DefaultExecutionStateProjector implements ExecutionStateProjector {

    @Override
    public Map<NodeId, NodeState> projectNodeStates(List<ExecutionEvent> events) {
        Map<NodeId, NodeState> nodeStates = new HashMap<>();
        if (events == null) return nodeStates;

        for (ExecutionEvent event : events) {
            if (event.nodeId().isEmpty()) continue;
            NodeId nodeId = event.nodeId().get();

            switch (event.type()) {
                case NODE_READY -> nodeStates.put(nodeId, NodeState.READY);
                case NODE_ADMITTED -> nodeStates.put(nodeId, NodeState.ADMITTED);
                case NODE_STARTED -> nodeStates.put(nodeId, NodeState.RUNNING);
                case NODE_COMPLETED -> nodeStates.put(nodeId, NodeState.COMPLETED);
                case NODE_FAILED -> nodeStates.put(nodeId, NodeState.FAILED);
                case NODE_SKIPPED -> nodeStates.put(nodeId, NodeState.SKIPPED);
                default -> {}
            }
        }
        return Map.copyOf(nodeStates);
    }

    @Override
    public GraphState projectGraphState(List<ExecutionEvent> events) {
        if (events == null || events.isEmpty()) return GraphState.DRAFT;

        GraphState state = GraphState.DRAFT;
        for (ExecutionEvent event : events) {
            switch (event.type()) {
                case GRAPH_SUBMITTED -> state = GraphState.VALIDATING;
                case GRAPH_STARTED -> state = GraphState.RUNNING;
                case GRAPH_PAUSED -> state = GraphState.PAUSED;
                case GRAPH_RESUMED -> state = GraphState.RUNNING;
                case GRAPH_COMPLETED -> state = GraphState.COMPLETED;
                case GRAPH_FAILED -> state = GraphState.FAILED;
                case GRAPH_CANCELLED -> state = GraphState.CANCELLED;
                default -> {}
            }
        }
        return state;
    }
}
