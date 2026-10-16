package tech.kayys.andalus.spi.capability;

import java.util.List;
import java.util.Objects;

/**
 * A capability together with its graph relationships.
 *
 * <p>The node wraps the existing Capability SPI rather than replacing it.</p>
 */
public record CapabilityNode(
        Capability capability,
        List<CapabilityDependency> dependencies,
        List<String> parents
) {

    public CapabilityNode {
        Objects.requireNonNull(capability, "capability cannot be null");

        dependencies = dependencies == null
                ? List.of()
                : List.copyOf(dependencies);

        parents = parents == null
                ? List.of()
                : parents.stream()
                        .filter(Objects::nonNull)
                        .filter(value -> !value.isBlank())
                        .distinct()
                        .toList();
    }

    public String id() {
        return capability.id();
    }

    public CapabilityDescriptor descriptor() {
        return capability.descriptor();
    }

    public boolean dependsOn(String capabilityId) {
        return dependencies.stream()
                .anyMatch(dependency ->
                        dependency.capabilityId().equals(capabilityId));
    }

    public boolean isChildOf(String capabilityId) {
        return parents.contains(capabilityId);
    }
}
