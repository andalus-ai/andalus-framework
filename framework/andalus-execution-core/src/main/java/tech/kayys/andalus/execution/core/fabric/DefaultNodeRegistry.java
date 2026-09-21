package tech.kayys.andalus.execution.core.fabric;

import tech.kayys.andalus.execution.fabric.NodeIdentity;
import tech.kayys.andalus.execution.fabric.NodeRegistry;
import tech.kayys.andalus.execution.fabric.NodeState;
import tech.kayys.andalus.execution.fabric.NodeStatus;
import tech.kayys.andalus.execution.fabric.AndalusNode;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory NodeRegistry managing cluster nodes and their live states.
 */
public class DefaultNodeRegistry implements NodeRegistry {

    private final Map<String, AndalusNode> nodes = new ConcurrentHashMap<>();

    @Override
    public void register(AndalusNode node) {
        Objects.requireNonNull(node, "node cannot be null");
        nodes.put(node.identity().canonicalName(), node);
    }

    @Override
    public void heartbeat(NodeIdentity identity, double currentLoad) {
        Objects.requireNonNull(identity, "identity cannot be null");
        AndalusNode existing = nodes.get(identity.canonicalName());
        if (existing != null) {
            NodeStatus newStatus = new NodeStatus(NodeState.UP, Instant.now(), currentLoad, true);
            nodes.put(identity.canonicalName(), new DelegatingAndalusNode(existing, newStatus));
        }
    }

    @Override
    public void deregister(NodeIdentity identity) {
        if (identity != null) {
            nodes.remove(identity.canonicalName());
        }
    }

    @Override
    public Optional<AndalusNode> get(NodeIdentity identity) {
        if (identity == null) return Optional.empty();
        return Optional.ofNullable(nodes.get(identity.canonicalName()));
    }

    @Override
    public Collection<AndalusNode> allNodes() {
        return List.copyOf(nodes.values());
    }

    private static class DelegatingAndalusNode implements AndalusNode {
        private final AndalusNode delegate;
        private final NodeStatus status;

        public DelegatingAndalusNode(AndalusNode delegate, NodeStatus status) {
            this.delegate = delegate;
            this.status = status;
        }

        @Override public NodeIdentity identity() { return delegate.identity(); }
        @Override public tech.kayys.andalus.execution.fabric.NodeDescriptor descriptor() { return delegate.descriptor(); }
        @Override public NodeStatus status() { return status; }
        @Override public tech.kayys.andalus.execution.conformance.ConformanceProfile profile() { return delegate.profile(); }
        @Override public java.util.Set<tech.kayys.andalus.execution.resource.requirement.ResourceRequirement> availableResources() { return delegate.availableResources(); }
    }
}
