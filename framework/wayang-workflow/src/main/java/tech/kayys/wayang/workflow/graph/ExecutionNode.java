package tech.kayys.wayang.workflow.graph;

import java.util.Optional;

/**
 * Represents a unit of execution within an execution graph.
 */
public interface ExecutionNode {

    NodeId id();

    NodeType type();

    Optional<Object> operation();

    default <T> Optional<T> operation(Class<T> type) {
        return operation().filter(type::isInstance).map(type::cast);
    }

    NodeConstraints constraints();

    NodeMetadata metadata();
}
