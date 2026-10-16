package tech.kayys.andalus.spi.capability;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Immutable capability relationship graph.
 *
 * <p>The graph describes capability hierarchy and dependencies. It does not
 * perform authorization, policy evaluation, or provider selection.</p>
 */
public final class CapabilityGraph {

    private final Map<String, CapabilityNode> nodes;

    public CapabilityGraph(Map<String, CapabilityNode> nodes) {
        Objects.requireNonNull(nodes, "nodes cannot be null");

        this.nodes = Collections.unmodifiableMap(
                new LinkedHashMap<>(nodes)
        );

        validateReferences();
        validateAcyclic();
    }

    public static CapabilityGraph empty() {
        return new CapabilityGraph(Map.of());
    }

    public Optional<CapabilityNode> find(String capabilityId) {
        if (capabilityId == null || capabilityId.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(nodes.get(capabilityId));
    }

    public boolean contains(String capabilityId) {
        return nodes.containsKey(capabilityId);
    }

    public List<CapabilityNode> nodes() {
        return List.copyOf(nodes.values());
    }

    public Set<String> capabilityIds() {
        return Set.copyOf(nodes.keySet());
    }

    /**
     * Returns direct dependencies of a capability.
     */
    public List<CapabilityDependency> dependenciesOf(String capabilityId) {
        return find(capabilityId)
                .map(CapabilityNode::dependencies)
                .orElse(List.of());
    }

    /**
     * Expands a capability into all transitive dependencies.
     */
    public Set<String> expand(String capabilityId) {
        if (!contains(capabilityId)) {
            return Set.of();
        }

        Set<String> expanded = new LinkedHashSet<>();
        Deque<String> queue = new ArrayDeque<>();

        queue.add(capabilityId);

        while (!queue.isEmpty()) {
            String current = queue.removeFirst();

            if (!expanded.add(current)) {
                continue;
            }

            CapabilityNode node = nodes.get(current);

            for (CapabilityDependency dependency : node.dependencies()) {
                queue.addLast(dependency.capabilityId());
            }
        }

        return Collections.unmodifiableSet(expanded);
    }

    /**
     * Returns all ancestors through the parent hierarchy.
     */
    public Set<String> ancestorsOf(String capabilityId) {
        Set<String> result = new LinkedHashSet<>();
        Deque<String> queue = new ArrayDeque<>();

        find(capabilityId)
                .ifPresent(node -> queue.addAll(node.parents()));

        while (!queue.isEmpty()) {
            String current = queue.removeFirst();

            if (!result.add(current)) {
                continue;
            }

            find(current).ifPresent(node ->
                    queue.addAll(node.parents()));
        }

        return Collections.unmodifiableSet(result);
    }

    private void validateReferences() {
        for (CapabilityNode node : nodes.values()) {

            for (CapabilityDependency dependency : node.dependencies()) {
                if (!nodes.containsKey(dependency.capabilityId())) {
                    throw new IllegalArgumentException(
                            "Capability '%s' depends on unknown capability '%s'"
                                    .formatted(node.id(), dependency.capabilityId())
                    );
                }
            }

            for (String parent : node.parents()) {
                if (!nodes.containsKey(parent)) {
                    throw new IllegalArgumentException(
                            "Capability '%s' references unknown parent '%s'"
                                    .formatted(node.id(), parent)
                    );
                }
            }
        }
    }

    private void validateAcyclic() {
        validateDependencyAcyclic();
        validateHierarchyAcyclic();
    }

    private void validateDependencyAcyclic() {
        Set<String> visiting = new LinkedHashSet<>();
        Set<String> visited = new LinkedHashSet<>();

        for (String capabilityId : nodes.keySet()) {
            detectDependencyCycle(capabilityId, visiting, visited);
        }
    }

    private void detectDependencyCycle(
            String capabilityId,
            Set<String> visiting,
            Set<String> visited) {

        if (visited.contains(capabilityId)) {
            return;
        }

        if (!visiting.add(capabilityId)) {
            throw new IllegalArgumentException(
                    "Capability graph contains a cycle involving '%s'"
                            .formatted(capabilityId)
            );
        }

        CapabilityNode node = nodes.get(capabilityId);

        for (CapabilityDependency dependency : node.dependencies()) {
            detectDependencyCycle(dependency.capabilityId(), visiting, visited);
        }

        visiting.remove(capabilityId);
        visited.add(capabilityId);
    }

    private void validateHierarchyAcyclic() {
        Set<String> visiting = new LinkedHashSet<>();
        Set<String> visited = new LinkedHashSet<>();

        for (String capabilityId : nodes.keySet()) {
            detectHierarchyCycle(capabilityId, visiting, visited);
        }
    }

    private void detectHierarchyCycle(
            String capabilityId,
            Set<String> visiting,
            Set<String> visited) {

        if (visited.contains(capabilityId)) {
            return;
        }

        if (!visiting.add(capabilityId)) {
            throw new IllegalArgumentException(
                    "Capability hierarchy contains a cycle involving '%s'"
                            .formatted(capabilityId)
            );
        }

        CapabilityNode node = nodes.get(capabilityId);

        for (String parent : node.parents()) {
            detectHierarchyCycle(parent, visiting, visited);
        }

        visiting.remove(capabilityId);
        visited.add(capabilityId);
    }
}
