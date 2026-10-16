package tech.kayys.andalus.spi.capability;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Builder for immutable CapabilityGraph instances.
 */
public final class CapabilityGraphBuilder {

    private final Map<String, Capability> capabilities =
            new LinkedHashMap<>();

    private final Map<String, Set<CapabilityDependency>> dependencies =
            new LinkedHashMap<>();

    private final Map<String, Set<String>> parents =
            new LinkedHashMap<>();

    private CapabilityGraphBuilder() {
    }

    public static CapabilityGraphBuilder builder() {
        return new CapabilityGraphBuilder();
    }

    public CapabilityGraphBuilder capability(Capability capability) {
        Objects.requireNonNull(capability, "capability cannot be null");

        capabilities.put(capability.id(), capability);
        return this;
    }

    public CapabilityGraphBuilder dependency(
            String capabilityId,
            CapabilityDependency dependency) {

        Objects.requireNonNull(capabilityId, "capabilityId cannot be null");
        Objects.requireNonNull(dependency, "dependency cannot be null");

        dependencies
                .computeIfAbsent(
                        capabilityId,
                        ignored -> new LinkedHashSet<>())
                .add(dependency);

        return this;
    }

    public CapabilityGraphBuilder requiredDependency(
            String capabilityId,
            String dependencyId) {

        return dependency(
                capabilityId,
                CapabilityDependency.required(dependencyId));
    }

    public CapabilityGraphBuilder optionalDependency(
            String capabilityId,
            String dependencyId) {

        return dependency(
                capabilityId,
                CapabilityDependency.optional(dependencyId));
    }

    public CapabilityGraphBuilder parent(
            String capabilityId,
            String parentId) {

        Objects.requireNonNull(capabilityId, "capabilityId cannot be null");
        Objects.requireNonNull(parentId, "parentId cannot be null");

        parents
                .computeIfAbsent(
                        capabilityId,
                        ignored -> new LinkedHashSet<>())
                .add(parentId);

        return this;
    }

    public CapabilityGraph build() {
        Map<String, CapabilityNode> nodes =
                new LinkedHashMap<>();

        for (Map.Entry<String, Capability> entry : capabilities.entrySet()) {

            String id = entry.getKey();

            nodes.put(
                    id,
                    new CapabilityNode(
                            entry.getValue(),
                            List.copyOf(
                                    dependencies.getOrDefault(
                                            id,
                                            Set.of())),
                            List.copyOf(
                                    parents.getOrDefault(
                                            id,
                                            Set.of()))
                    )
            );
        }

        return new CapabilityGraph(nodes);
    }
}
