package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.workflow.graph.GraphState;
import tech.kayys.wayang.workflow.graph.NodeId;
import tech.kayys.wayang.workflow.graph.NodeState;

import java.util.List;
import java.util.Map;

/**
 * Deterministic state projector reconstructing runtime states purely from event sequences.
 */
public interface ExecutionStateProjector {

    Map<NodeId, NodeState> projectNodeStates(List<ExecutionEvent> events);

    GraphState projectGraphState(List<ExecutionEvent> events);
}
