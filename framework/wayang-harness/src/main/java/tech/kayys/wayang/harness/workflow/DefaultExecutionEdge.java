package tech.kayys.wayang.harness.workflow;

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable reference record implementing {@link ExecutionEdge}.
 */
public record DefaultExecutionEdge(
        NodeId from,
        NodeId to,
        EdgeType type,
        EdgeCondition condition,
        Optional<DataBinding> binding,
        DependencySemantics semantics
) implements ExecutionEdge {

    public DefaultExecutionEdge {
        Objects.requireNonNull(from, "from node cannot be null");
        Objects.requireNonNull(to, "to node cannot be null");
        type = type != null ? type : EdgeType.DEPENDS_ON;
        condition = condition != null ? condition : EdgeCondition.alwaysTrue();
        binding = binding != null ? binding : Optional.empty();
        semantics = semantics != null ? semantics : DependencySemantics.HARD;
    }

    public static DefaultExecutionEdge dependsOn(NodeId from, NodeId to) {
        return new DefaultExecutionEdge(from, to, EdgeType.DEPENDS_ON, EdgeCondition.alwaysTrue(), Optional.empty(), DependencySemantics.HARD);
    }

    public static DefaultExecutionEdge dependsOn(NodeId from, NodeId to, DependencySemantics semantics) {
        return new DefaultExecutionEdge(from, to, EdgeType.DEPENDS_ON, EdgeCondition.alwaysTrue(), Optional.empty(), semantics);
    }

    public static DefaultExecutionEdge dataFlow(NodeId from, NodeId to, DataBinding binding) {
        return new DefaultExecutionEdge(from, to, EdgeType.DATA, EdgeCondition.alwaysTrue(), Optional.ofNullable(binding), DependencySemantics.HARD);
    }

    public static DefaultExecutionEdge condition(NodeId from, NodeId to, EdgeCondition condition) {
        return new DefaultExecutionEdge(from, to, EdgeType.CONDITION, condition, Optional.empty(), DependencySemantics.HARD);
    }
}
