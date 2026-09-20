package tech.kayys.wayang.execution.conformance;

import java.util.Objects;
import java.util.Set;

/**
 * Result of resolving a set of capability requirements against available descriptors.
 */
public record CapabilityResolution(
    boolean satisfied,
    Set<CapabilityDescriptor> resolvedCapabilities,
    Set<CapabilityRequirement> unsatisfiedRequirements
) {
    public CapabilityResolution {
        resolvedCapabilities = resolvedCapabilities != null ? Set.copyOf(resolvedCapabilities) : Set.of();
        unsatisfiedRequirements = unsatisfiedRequirements != null ? Set.copyOf(unsatisfiedRequirements) : Set.of();
    }

    public static CapabilityResolution success(Set<CapabilityDescriptor> resolved) {
        return new CapabilityResolution(true, resolved, Set.of());
    }

    public static CapabilityResolution failure(Set<CapabilityDescriptor> resolved, Set<CapabilityRequirement> unsatisfied) {
        return new CapabilityResolution(false, resolved, unsatisfied);
    }
}
