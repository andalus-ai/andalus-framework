package tech.kayys.wayang.harness.resource;

import tech.kayys.wayang.harness.environment.ResourceId;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default resource allocator coordinating provider resolution and lease tracking.
 */
public class DefaultResourceAllocator implements ResourceAllocator {

    private final ResourceProviderRegistry registry;
    private final Map<String, ResourceLease> activeLeases = new ConcurrentHashMap<>();

    public DefaultResourceAllocator(ResourceProviderRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public ResourceLease acquire(ResourceRequest request) {
        Objects.requireNonNull(request, "request");
        ResourceProvider provider = registry.findProvider(request)
                .orElseGet(() -> new FallbackResourceProvider(request.type()));

        ResourceLease lease = provider.acquire(request);
        activeLeases.put(lease.id(), lease);
        return lease;
    }

    @Override
    public void release(ResourceLease lease) {
        if (lease != null) {
            activeLeases.remove(lease.id());
            lease.close();
        }
    }

    public Map<String, ResourceLease> activeLeases() {
        return Map.copyOf(activeLeases);
    }

    private static class FallbackResourceProvider implements ResourceProvider {
        private final ResourceType type;

        FallbackResourceProvider(ResourceType type) {
            this.type = type;
        }

        @Override public ResourceType type() { return type; }
        @Override public boolean supports(ResourceRequest request) { return true; }
        @Override
        public ResourceLease acquire(ResourceRequest request) {
            String id = request.resourceId() != null ? request.resourceId() : "res-" + type.name().toLowerCase();
            HarnessResource res = new DefaultHarnessResource(ResourceId.of(id), type, ResourceStatus.ALLOCATED, ResourceMetadata.empty());
            return new DefaultResourceLease(res, Instant.now().plusSeconds(3600), () -> {});
        }
    }
}
