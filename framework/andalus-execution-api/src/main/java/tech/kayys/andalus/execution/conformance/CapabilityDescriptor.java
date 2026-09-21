package tech.kayys.andalus.execution.conformance;

import java.util.Objects;
import java.util.Set;

/**
 * Formal descriptor of a verified or declared execution capability.
 */
public record CapabilityDescriptor(
    CapabilityId id,
    CapabilityVersion version,
    CapabilityLevel level,
    Set<Guarantee> guarantees,
    Set<CapabilityConstraint> constraints,
    Set<CapabilityId> dependencies
) {

    public CapabilityDescriptor {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(version, "version cannot be null");
        Objects.requireNonNull(level, "level cannot be null");
        guarantees = guarantees != null ? Set.copyOf(guarantees) : Set.of();
        constraints = constraints != null ? Set.copyOf(constraints) : Set.of();
        dependencies = dependencies != null ? Set.copyOf(dependencies) : Set.of();
    }

    public static CapabilityDescriptor of(CapabilityId id, CapabilityVersion version, CapabilityLevel level) {
        return new CapabilityDescriptor(id, version, level, Set.of(), Set.of(), Set.of());
    }

    public static CapabilityDescriptor of(CapabilityId id, CapabilityVersion version, CapabilityLevel level, Set<Guarantee> guarantees) {
        return new CapabilityDescriptor(id, version, level, guarantees, Set.of(), Set.of());
    }

    public boolean provides(Guarantee guarantee) {
        return guarantees.contains(guarantee);
    }
}
