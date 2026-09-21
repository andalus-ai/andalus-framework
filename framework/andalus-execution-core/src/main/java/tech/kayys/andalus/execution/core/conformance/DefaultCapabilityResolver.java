package tech.kayys.andalus.execution.core.conformance;

import tech.kayys.andalus.execution.conformance.CapabilityDescriptor;
import tech.kayys.andalus.execution.conformance.CapabilityRegistry;
import tech.kayys.andalus.execution.conformance.CapabilityRequirement;
import tech.kayys.andalus.execution.conformance.CapabilityResolution;
import tech.kayys.andalus.execution.conformance.CapabilityResolver;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Default implementation of CapabilityResolver querying a CapabilityRegistry.
 */
public class DefaultCapabilityResolver implements CapabilityResolver {

    private final CapabilityRegistry registry;

    public DefaultCapabilityResolver(CapabilityRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry cannot be null");
    }

    @Override
    public CapabilityResolution resolve(Set<CapabilityRequirement> requirements) {
        if (requirements == null || requirements.isEmpty()) {
            return CapabilityResolution.success(Set.of());
        }

        Set<CapabilityDescriptor> resolved = new HashSet<>();
        Set<CapabilityRequirement> unsatisfied = new HashSet<>();

        for (CapabilityRequirement req : requirements) {
            Optional<CapabilityDescriptor> found = registry.resolve(req.requiredCapability());
            if (found.isPresent() && req.satisfiedBy(found.get())) {
                resolved.add(found.get());
            } else {
                unsatisfied.add(req);
            }
        }

        if (unsatisfied.isEmpty()) {
            return CapabilityResolution.success(resolved);
        } else {
            return CapabilityResolution.failure(resolved, unsatisfied);
        }
    }
}
