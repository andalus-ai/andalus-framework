package tech.kayys.wayang.harness.environment;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory thread-safe implementation of {@link HarnessResources}.
 */
public class DefaultHarnessResources implements HarnessResources {

    private final Map<ResourceId, ResourceHandle> resources = new ConcurrentHashMap<>();

    public DefaultHarnessResources() {}

    public DefaultHarnessResources(Map<ResourceId, ResourceHandle> initial) {
        if (initial != null) {
            resources.putAll(initial);
        }
    }

    public void register(ResourceHandle handle) {
        resources.put(handle.id(), handle);
    }

    @Override
    public Optional<ResourceHandle> find(ResourceId id) {
        return Optional.ofNullable(resources.get(id));
    }

    @Override
    public ResourceHandle require(ResourceId id) {
        return find(id).orElseThrow(() -> new NoSuchElementException("Required resource not found: " + id.value()));
    }

    @Override
    public Set<ResourceId> available() {
        return Set.copyOf(resources.keySet());
    }
}
