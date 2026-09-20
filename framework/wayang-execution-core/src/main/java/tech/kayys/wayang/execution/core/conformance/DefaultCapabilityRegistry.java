package tech.kayys.wayang.execution.core.conformance;

import tech.kayys.wayang.execution.conformance.CapabilityDescriptor;
import tech.kayys.wayang.execution.conformance.CapabilityId;
import tech.kayys.wayang.execution.conformance.CapabilityRegistry;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory capability registry.
 */
public class DefaultCapabilityRegistry implements CapabilityRegistry {

    private final Map<CapabilityId, CapabilityDescriptor> registry = new ConcurrentHashMap<>();

    @Override
    public void register(CapabilityDescriptor descriptor) {
        Objects.requireNonNull(descriptor, "descriptor cannot be null");
        registry.put(descriptor.id(), descriptor);
    }

    @Override
    public Optional<CapabilityDescriptor> resolve(CapabilityId id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(registry.get(id));
    }

    @Override
    public Collection<CapabilityDescriptor> capabilities() {
        return List.copyOf(registry.values());
    }
}
