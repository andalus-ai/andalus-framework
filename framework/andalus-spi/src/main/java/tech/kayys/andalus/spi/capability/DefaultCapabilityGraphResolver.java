package tech.kayys.andalus.spi.capability;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class DefaultCapabilityGraphResolver
        implements CapabilityGraphResolver {

    @Override
    public CapabilityGraphResolution resolve(
            CapabilityRequirements requirements,
            CapabilityGraph graph) {

        Objects.requireNonNull(requirements, "requirements cannot be null");
        Objects.requireNonNull(graph, "graph cannot be null");

        Set<String> requested = new LinkedHashSet<>();
        Set<String> required = new LinkedHashSet<>();
        Set<String> optional = new LinkedHashSet<>();
        Set<String> expanded = new LinkedHashSet<>();
        Set<String> missing = new LinkedHashSet<>();

        for (CapabilityRequirement requirement :
                requirements.requirements()) {

            String id = requirement.capabilityId();

            requested.add(id);

            if (requirement.required()) {
                required.add(id);
            } else {
                optional.add(id);
            }

            if (!graph.contains(id)) {
                if (requirement.required()) {
                    missing.add(id);
                }

                continue;
            }

            expanded.addAll(graph.expand(id));
        }

        /*
         * A required capability's required dependencies must also exist.
         */
        for (String id : new LinkedHashSet<>(expanded)) {

            CapabilityNode node = graph.find(id).orElse(null);

            if (node == null) {
                continue;
            }

            for (CapabilityDependency dependency :
                    node.dependencies()) {

                if (dependency.required()
                        && !graph.contains(dependency.capabilityId())) {

                    missing.add(dependency.capabilityId());
                }
            }
        }

        return new CapabilityGraphResolution(
                requested,
                expanded,
                required,
                optional,
                missing
        );
    }
}
