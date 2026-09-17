package tech.kayys.wayang.harness.resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry indexing resource providers by resource type.
 */
public class ResourceProviderRegistry {

    private final Map<ResourceType, List<ResourceProvider>> providers = new ConcurrentHashMap<>();

    public void register(ResourceProvider provider) {
        providers.computeIfAbsent(provider.type(), t -> new CopyOnWriteArrayList<>()).add(provider);
    }

    public Optional<ResourceProvider> findProvider(ResourceRequest request) {
        List<ResourceProvider> candidates = providers.getOrDefault(request.type(), List.of());
        return candidates.stream().filter(p -> p.supports(request)).findFirst();
    }
}
