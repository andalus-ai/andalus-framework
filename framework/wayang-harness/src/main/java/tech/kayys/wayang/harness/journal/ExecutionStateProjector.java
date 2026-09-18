package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.harness.workflow.GraphState;
import tech.kayys.wayang.harness.workflow.NodeId;
import tech.kayys.wayang.harness.workflow.NodeState;

import java.util.List;
import java.util.Map;

/**
 * Deterministic state projector reconstructing runtime states purely from event sequences.
 */
public interface ExecutionStateProjector {

    Map<NodeId, NodeState> projectNodeStates(List<ExecutionEvent> events);

    GraphState projectGraphState(List<ExecutionEvent> events);
}
