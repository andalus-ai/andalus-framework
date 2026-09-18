package tech.kayys.wayang.workflow.graph;

import java.util.Map;

/**
 * Evaluates execution graph state and determines which nodes are eligible for scheduling.
 */
public interface GraphScheduler {

    ReadySet computeReadySet(ExecutionGraph graph, Map<NodeId, NodeState> nodeStates);

    boolean isGraphFinished(ExecutionGraph graph, Map<NodeId, NodeState> nodeStates);

    GraphState deriveGraphState(ExecutionGraph graph, Map<NodeId, NodeState> nodeStates);
}
