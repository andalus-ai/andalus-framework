package tech.kayys.andalus.execution.conformance;

import java.util.Objects;
import java.util.Set;

/**
 * Requirement expression evaluating whether a capability descriptor satisfies demands.
 */
public interface CapabilityRequirement {

    CapabilityId requiredCapability();

    boolean satisfiedBy(CapabilityDescriptor descriptor);

    static CapabilityRequirement of(CapabilityId id, CapabilityLevel minimumLevel) {
        return new LevelRequirement(id, minimumLevel, Set.of());
    }

    static CapabilityRequirement of(CapabilityId id, CapabilityLevel minimumLevel, Set<Guarantee> requiredGuarantees) {
        return new LevelRequirement(id, minimumLevel, requiredGuarantees);
    }

    record LevelRequirement(
        CapabilityId requiredCapability,
        CapabilityLevel minimumLevel,
        Set<Guarantee> requiredGuarantees
    ) implements CapabilityRequirement {

        public LevelRequirement {
            Objects.requireNonNull(requiredCapability, "requiredCapability cannot be null");
            Objects.requireNonNull(minimumLevel, "minimumLevel cannot be null");
            requiredGuarantees = requiredGuarantees != null ? Set.copyOf(requiredGuarantees) : Set.of();
        }

        @Override
        public boolean satisfiedBy(CapabilityDescriptor descriptor) {
            if (descriptor == null || !descriptor.id().equals(requiredCapability)) {
                return false;
            }
            if (!descriptor.level().satisfies(minimumLevel)) {
                return false;
            }
            return descriptor.guarantees().containsAll(requiredGuarantees);
        }
    }
}
