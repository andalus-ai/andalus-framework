package tech.kayys.wayang.harness.environment.v3.resource;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe default implementation of {@link ResourceRuntime}.
 */
public class DefaultResourceRuntime implements ResourceRuntime {

    private final ResourceCatalog catalog;
    private final Map<LeaseId, ResourceLease> activeLeases = new ConcurrentHashMap<>();
    private final Map<ExecutionId, ResourceUsage> usages = new ConcurrentHashMap<>();

    public DefaultResourceRuntime(ResourceCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    @Override
    public ResourceCatalog catalog() {
        return catalog;
    }

    @Override
    public ResourceLease acquire(ExecutionId executionId, ResourceRequest request) {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(request, "request");

        // Locate available resource matching requested type
        ResourceDescriptor descriptor = catalog.findByType(request.type()).stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No available resource found for type: " + request.type()));

        LeaseId leaseId = LeaseId.generate();
        long maxCap = request.requirements().requiredCapacity() > 0 
                ? request.requirements().requiredCapacity() 
                : descriptor.capacity();

        DefaultResourceLease lease = new DefaultResourceLease(
                leaseId,
                descriptor.id(),
                executionId,
                LeaseConstraints.ofCapacity(maxCap),
                Instant.now().plusSeconds(3600),
                activeLeases::remove
        );

        activeLeases.put(leaseId, lease);
        return lease;
    }

    @Override
    public ResourceUsage usage(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId");
        return usages.getOrDefault(executionId, ResourceUsage.zero(executionId));
    }

    public void recordUsage(ExecutionId executionId, ResourceUsage usage) {
        usages.put(executionId, usage);
    }
}
