package tech.kayys.wayang.harness.workflow;

import java.util.Objects;
import java.util.Optional;

/**
 * Specialized execution node that evaluates a dynamic condition and branches to subsequent nodes.
 */
public record ConditionNode(
        NodeId id,
        ConditionExpression condition,
        BranchSet branches,
        NodeMetadata metadata
) implements ExecutionNode {

    public ConditionNode {
        Objects.requireNonNull(id, "NodeId cannot be null");
        Objects.requireNonNull(condition, "Condition cannot be null");
        Objects.requireNonNull(branches, "Branches cannot be null");
        metadata = metadata != null ? metadata : NodeMetadata.of(id.value(), "Condition: " + condition.variable());
    }

    @Override
    public NodeType type() {
        return NodeType.CONDITION;
    }

    @Override
    public Optional<tech.kayys.wayang.harness.semantics.Operation> operation() {
        return Optional.empty();
    }

    @Override
    public NodeConstraints constraints() {
        return NodeConstraints.defaults();
    }
}
