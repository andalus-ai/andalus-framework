package tech.kayys.wayang.harness.workflow;

import tech.kayys.wayang.harness.semantics.Operation;

import java.util.Optional;

/**
 * Represents a unit of execution within an execution graph.
 */
public interface ExecutionNode {

    NodeId id();

    NodeType type();

    Optional<Operation> operation();

    NodeConstraints constraints();

    NodeMetadata metadata();
}
