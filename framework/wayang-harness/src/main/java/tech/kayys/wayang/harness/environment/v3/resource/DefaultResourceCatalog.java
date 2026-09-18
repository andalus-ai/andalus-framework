package tech.kayys.wayang.harness.environment.v3.resource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe default in-memory implementation of {@link ResourceCatalog}.
 */
public class DefaultResourceCatalog implements ResourceCatalog {

    private final Map<ResourceId, ResourceDescriptor> descriptors = new ConcurrentHashMap<>();

    public DefaultResourceCatalog() {}

    public DefaultResourceCatalog(Collection<ResourceDescriptor> initial) {
        if (initial != null) {
            initial.forEach(this::register);
        }
    }

    public void register(ResourceDescriptor descriptor) {
        if (descriptor != null) {
            descriptors.put(descriptor.id(), descriptor);
        }
    }

    public void unregister(ResourceId id) {
        if (id != null) {
            descriptors.remove(id);
        }
    }

    @Override
    public Collection<ResourceDescriptor> available() {
        return List.copyOf(descriptors.values());
    }

    @Override
    public Optional<ResourceDescriptor> find(ResourceId id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(descriptors.get(id));
    }

    @Override
    public Collection<ResourceDescriptor> findByType(ResourceType type) {
        if (type == null) {
            return List.of();
        }
        return descriptors.values().stream()
                .filter(d -> d.type() == type)
                .toList();
    }
}
