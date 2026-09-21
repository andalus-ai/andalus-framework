package tech.kayys.andalus.harness.journal;

import tech.kayys.andalus.workflow.graph.GraphState;
import tech.kayys.andalus.workflow.graph.NodeId;
import tech.kayys.andalus.workflow.graph.NodeState;

import java.util.List;
import java.util.Map;

/**
 * Deterministic state projector reconstructing runtime states purely from event sequences.
 */
public interface ExecutionStateProjector {

    Map<NodeId, NodeState> projectNodeStates(List<ExecutionEvent> events);

    /**
     * Projects graph state starting from {@link GraphState#DRAFT}. Equivalent to
     * {@code projectGraphState(GraphState.DRAFT, events)}.
     */
    GraphState projectGraphState(List<ExecutionEvent> events);

    /**
     * Projects graph state starting from {@code seedState} and applying each event in sequence.
     * Used during crash recovery to correctly apply a journal tail on top of a checkpoint's
     * already-known graph state, avoiding the bug where the tail starts from DRAFT.
     */
    default GraphState projectGraphState(GraphState seedState, List<ExecutionEvent> events) {
        return projectGraphState(events);
    }
}
