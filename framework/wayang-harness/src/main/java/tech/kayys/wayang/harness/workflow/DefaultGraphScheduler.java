package tech.kayys.wayang.harness.workflow;

import java.util.*;

/**
 * Standard implementation of {@link GraphScheduler}.
 */
public class DefaultGraphScheduler implements GraphScheduler {

    @Override
    public ReadySet computeReadySet(ExecutionGraph graph, Map<NodeId, NodeState> nodeStates) {
        Objects.requireNonNull(graph, "ExecutionGraph cannot be null");
        Map<NodeId, NodeState> states = nodeStates != null ? nodeStates : Map.of();

        Set<ExecutionNode> ready = new LinkedHashSet<>();
        Set<NodeId> blocked = new LinkedHashSet<>();

        for (ExecutionNode node : graph.nodes()) {
            NodeState currentState = states.getOrDefault(node.id(), NodeState.PENDING);
            if (currentState != NodeState.PENDING) {
                // Already started or terminal
                continue;
            }

            boolean dependenciesSatisfied = true;
            for (ExecutionEdge edge : graph.incomingEdges(node.id())) {
                NodeState fromState = states.getOrDefault(edge.from(), NodeState.PENDING);
                boolean satisfied = switch (edge.semantics()) {
                    case HARD -> fromState == NodeState.COMPLETED;
                    case SOFT -> fromState == NodeState.COMPLETED || fromState == NodeState.SKIPPED;
                    case OPTIONAL -> fromState.isTerminal();
                    case EXCLUSIVE -> fromState == NodeState.COMPLETED;
                };

                if (!satisfied) {
                    dependenciesSatisfied = false;
                    break;
                }
            }

            if (dependenciesSatisfied) {
                ready.add(node);
            } else {
                blocked.add(node.id());
            }
        }

        return ReadySet.of(ready, blocked);
    }

    @Override
    public boolean isGraphFinished(ExecutionGraph graph, Map<NodeId, NodeState> nodeStates) {
        if (graph == null || graph.nodes().isEmpty()) return true;
        Map<NodeId, NodeState> states = nodeStates != null ? nodeStates : Map.of();
        return graph.nodes().stream().allMatch(n -> states.getOrDefault(n.id(), NodeState.PENDING).isTerminal());
    }

    @Override
    public GraphState deriveGraphState(ExecutionGraph graph, Map<NodeId, NodeState> nodeStates) {
        if (graph == null || graph.nodes().isEmpty()) return GraphState.COMPLETED;
        Map<NodeId, NodeState> states = nodeStates != null ? nodeStates : Map.of();

        boolean hasFailed = false;
        boolean hasRunning = false;
        boolean allTerminal = true;

        for (ExecutionNode node : graph.nodes()) {
            NodeState s = states.getOrDefault(node.id(), NodeState.PENDING);
            if (s == NodeState.FAILED) {
                hasFailed = true;
            }
            if (s == NodeState.RUNNING || s == NodeState.ADMITTED) {
                hasRunning = true;
            }
            if (!s.isTerminal()) {
                allTerminal = false;
            }
        }

        if (hasFailed) return GraphState.FAILED;
        if (allTerminal) return GraphState.COMPLETED;
        if (hasRunning) return GraphState.RUNNING;
        return GraphState.READY;
    }
}
