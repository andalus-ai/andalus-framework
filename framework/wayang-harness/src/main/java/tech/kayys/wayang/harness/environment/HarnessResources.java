package tech.kayys.wayang.harness.environment;

import tech.kayys.wayang.harness.resource.ResourceAllocator;
import tech.kayys.wayang.harness.resource.ResourceQuota;
import tech.kayys.wayang.harness.workspace.Workspace;

import java.util.Optional;
import java.util.Set;

/**
 * SPI for locating, managing, and allocating resources in the agent's execution environment.
 */
public interface HarnessResources {

    Workspace workspace();

    ResourceAllocator allocator();

    ResourceQuota quota();

    default Optional<ResourceHandle> find(ResourceId id) {
        return Optional.empty();
    }

    default ResourceHandle require(ResourceId id) {
        return find(id).orElseThrow(() -> new java.util.NoSuchElementException("Required resource not found: " + id.value()));
    }

    default Set<ResourceId> available() {
        return Set.of();
    }
}
