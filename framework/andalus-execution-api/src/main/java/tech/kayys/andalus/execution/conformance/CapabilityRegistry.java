package tech.kayys.andalus.execution.conformance;

import java.util.Collection;
import java.util.Optional;

/**
 * Registry storing available execution capability descriptors.
 */
public interface CapabilityRegistry {

    void register(CapabilityDescriptor descriptor);

    Optional<CapabilityDescriptor> resolve(CapabilityId id);

    Collection<CapabilityDescriptor> capabilities();
}
