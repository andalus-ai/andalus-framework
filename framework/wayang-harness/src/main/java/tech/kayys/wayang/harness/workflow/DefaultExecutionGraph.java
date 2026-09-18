package tech.kayys.wayang.harness.workflow;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable reference record implementing {@link ExecutionGraph}.
 */
public record DefaultExecutionGraph(
        ExecutionGraphId id,
        GraphVersion version,
        Set<ExecutionNode> nodes,
        Set<ExecutionEdge> edges,
        GraphMetadata metadata,
        GraphConstraints constraints
) implements ExecutionGraph {

    public DefaultExecutionGraph {
        Objects.requireNonNull(id, "ExecutionGraphId cannot be null");
        version = version != null ? version : GraphVersion.initial();
        nodes = nodes != null ? Set.copyOf(nodes) : Set.of();
        edges = edges != null ? Set.copyOf(edges) : Set.of();
        metadata = metadata != null ? metadata : GraphMetadata.of(id.value());
        constraints = constraints != null ? constraints : GraphConstraints.defaults();
    }

    @Override
    public Optional<ExecutionNode> node(NodeId id) {
        if (id == null) return Optional.empty();
        return nodes.stream().filter(n -> n.id().equals(id)).findFirst();
    }

    @Override
    public Set<ExecutionEdge> incomingEdges(NodeId id) {
        if (id == null) return Set.of();
        return edges.stream().filter(e -> e.to().equals(id)).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<ExecutionEdge> outgoingEdges(NodeId id) {
        if (id == null) return Set.of();
        return edges.stream().filter(e -> e.from().equals(id)).collect(Collectors.toUnmodifiableSet());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ExecutionGraphId id) {
        return new Builder().id(id);
    }

    public static class Builder {
        private ExecutionGraphId id = ExecutionGraphId.generate();
        private GraphVersion version = GraphVersion.initial();
        private final Map<NodeId, ExecutionNode> nodes = new LinkedHashMap<>();
        private final Set<ExecutionEdge> edges = new LinkedHashSet<>();
        private GraphMetadata metadata;
        private GraphConstraints constraints = GraphConstraints.defaults();

        public Builder id(ExecutionGraphId id) {
            this.id = Objects.requireNonNull(id);
            return this;
        }

        public Builder version(GraphVersion version) {
            this.version = Objects.requireNonNull(version);
            return this;
        }

        public Builder addNode(ExecutionNode node) {
            Objects.requireNonNull(node);
            this.nodes.put(node.id(), node);
            return this;
        }

        public Builder addEdge(ExecutionEdge edge) {
            Objects.requireNonNull(edge);
            this.edges.add(edge);
            return this;
        }

        public Builder dependsOn(NodeId from, NodeId to) {
            return addEdge(DefaultExecutionEdge.dependsOn(from, to));
        }

        public Builder metadata(GraphMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder constraints(GraphConstraints constraints) {
            this.constraints = constraints;
            return this;
        }

        public DefaultExecutionGraph build() {
            if (metadata == null) {
                metadata = GraphMetadata.of(id.value());
            }
            return new DefaultExecutionGraph(id, version, new LinkedHashSet<>(nodes.values()), edges, metadata, constraints);
        }
    }
}
