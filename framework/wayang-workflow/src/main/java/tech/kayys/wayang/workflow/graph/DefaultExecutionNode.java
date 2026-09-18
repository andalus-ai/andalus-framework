package tech.kayys.wayang.workflow.graph;

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable reference record implementing {@link ExecutionNode}.
 */
public record DefaultExecutionNode(
        NodeId id,
        NodeType type,
        Optional<Object> operation,
        NodeConstraints constraints,
        NodeMetadata metadata
) implements ExecutionNode {

    public DefaultExecutionNode {
        Objects.requireNonNull(id, "NodeId cannot be null");
        Objects.requireNonNull(type, "NodeType cannot be null");
        operation = operation != null ? operation : Optional.empty();
        constraints = constraints != null ? constraints : NodeConstraints.defaults();
        metadata = metadata != null ? metadata : NodeMetadata.of(id.value());
    }

    public static DefaultExecutionNode of(NodeId id, Object operation) {
        return new DefaultExecutionNode(
                id,
                NodeType.OPERATION,
                Optional.ofNullable(operation),
                NodeConstraints.defaults(),
                NodeMetadata.of(id.value())
        );
    }

    public static DefaultExecutionNode of(NodeId id, NodeType type) {
        return new DefaultExecutionNode(
                id,
                type,
                Optional.empty(),
                NodeConstraints.defaults(),
                NodeMetadata.of(id.value())
        );
    }
}
