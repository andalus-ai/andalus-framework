package tech.kayys.wayang.harness.workflow;

import java.util.Optional;
import java.util.Set;

/**
 * Immutable canonical execution graph describing operations, dependencies, and data flow.
 */
public interface ExecutionGraph {

    ExecutionGraphId id();

    GraphVersion version();

    Set<ExecutionNode> nodes();

    Set<ExecutionEdge> edges();

    GraphMetadata metadata();

    GraphConstraints constraints();

    Optional<ExecutionNode> node(NodeId id);

    Set<ExecutionEdge> incomingEdges(NodeId id);

    Set<ExecutionEdge> outgoingEdges(NodeId id);
}
