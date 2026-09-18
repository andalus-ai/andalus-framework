package tech.kayys.wayang.harness.workflow;

import java.util.Optional;

/**
 * Directed semantic edge between two nodes in an execution graph.
 */
public interface ExecutionEdge {

    NodeId from();

    NodeId to();

    EdgeType type();

    EdgeCondition condition();

    Optional<DataBinding> binding();

    DependencySemantics semantics();
}
