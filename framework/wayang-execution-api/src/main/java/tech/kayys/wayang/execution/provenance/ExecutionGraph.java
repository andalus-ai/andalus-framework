package tech.kayys.wayang.execution.provenance;

import java.util.Collection;
import java.util.Optional;

/**
 * Temporal Execution Graph representing full provenance and causality.
 */
public interface ExecutionGraph {

    String id();

    Collection<ExecutionNode> nodes();

    Collection<ExecutionEdge> edges();

    Optional<ExecutionNode> findNode(NodeId id);

    Collection<ExecutionEdge> outgoingEdges(NodeId id);

    Collection<ExecutionEdge> incomingEdges(NodeId id);
}
