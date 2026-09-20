package tech.kayys.wayang.execution.core.provenance;

import tech.kayys.wayang.execution.provenance.ExecutionEdge;
import tech.kayys.wayang.execution.provenance.ExecutionGraph;
import tech.kayys.wayang.execution.provenance.ExecutionNode;
import tech.kayys.wayang.execution.provenance.NodeId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Standard in-memory implementation of {@link ExecutionGraph}.
 */
public class DefaultExecutionGraph implements ExecutionGraph {

    private final String id;
    private final Map<NodeId, ExecutionNode> nodes = new LinkedHashMap<>();
    private final List<ExecutionEdge> edges = new ArrayList<>();

    public DefaultExecutionGraph(String id) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
    }

    public DefaultExecutionGraph addNode(ExecutionNode node) {
        Objects.requireNonNull(node, "node cannot be null");
        nodes.put(node.id(), node);
        return this;
    }

    public DefaultExecutionGraph addEdge(ExecutionEdge edge) {
        Objects.requireNonNull(edge, "edge cannot be null");
        edges.add(edge);
        return this;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Collection<ExecutionNode> nodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    @Override
    public Collection<ExecutionEdge> edges() {
        return Collections.unmodifiableList(edges);
    }

    @Override
    public Optional<ExecutionNode> findNode(NodeId id) {
        return Optional.ofNullable(nodes.get(id));
    }

    @Override
    public Collection<ExecutionEdge> outgoingEdges(NodeId id) {
        List<ExecutionEdge> out = new ArrayList<>();
        for (ExecutionEdge e : edges) {
            if (e.source().equals(id)) {
                out.add(e);
            }
        }
        return Collections.unmodifiableList(out);
    }

    @Override
    public Collection<ExecutionEdge> incomingEdges(NodeId id) {
        List<ExecutionEdge> in = new ArrayList<>();
        for (ExecutionEdge e : edges) {
            if (e.target().equals(id)) {
                in.add(e);
            }
        }
        return Collections.unmodifiableList(in);
    }
}
