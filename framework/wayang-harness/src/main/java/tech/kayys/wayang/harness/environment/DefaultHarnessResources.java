package tech.kayys.wayang.harness.environment;

import tech.kayys.wayang.harness.resource.DefaultResourceAllocator;
import tech.kayys.wayang.harness.resource.DefaultResourceQuota;
import tech.kayys.wayang.harness.resource.ResourceAllocator;
import tech.kayys.wayang.harness.resource.ResourceProviderRegistry;
import tech.kayys.wayang.harness.resource.ResourceQuota;
import tech.kayys.wayang.harness.workspace.LocalWorkspaceManager;
import tech.kayys.wayang.harness.workspace.Workspace;
import tech.kayys.wayang.harness.workspace.WorkspacePolicy;
import tech.kayys.wayang.harness.workspace.WorkspaceRequest;
import tech.kayys.wayang.harness.workspace.WorkspaceType;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link HarnessResources} integrating workspace boundary, resource allocation, and quota management.
 */
public class DefaultHarnessResources implements HarnessResources {

    private final Workspace workspace;
    private final ResourceAllocator allocator;
    private final ResourceQuota quota;
    private final Map<ResourceId, ResourceHandle> resources = new ConcurrentHashMap<>();

    public DefaultHarnessResources() {
        this(createDefaultWorkspace(), new DefaultResourceAllocator(new ResourceProviderRegistry()), DefaultResourceQuota.unlimited(), Map.of());
    }

    public DefaultHarnessResources(Workspace workspace, ResourceAllocator allocator, ResourceQuota quota) {
        this(workspace, allocator, quota, Map.of());
    }

    public DefaultHarnessResources(Workspace workspace, ResourceAllocator allocator, ResourceQuota quota, Map<ResourceId, ResourceHandle> initial) {
        this.workspace = Objects.requireNonNull(workspace, "workspace");
        this.allocator = Objects.requireNonNull(allocator, "allocator");
        this.quota = Objects.requireNonNull(quota, "quota");
        if (initial != null) {
            this.resources.putAll(initial);
        }
    }

    public DefaultHarnessResources(Map<ResourceId, ResourceHandle> initial) {
        this(createDefaultWorkspace(), new DefaultResourceAllocator(new ResourceProviderRegistry()), DefaultResourceQuota.unlimited(), initial);
    }

    private static Workspace createDefaultWorkspace() {
        LocalWorkspaceManager manager = new LocalWorkspaceManager();
        return manager.create(new WorkspaceRequest("default", WorkspaceType.LOCAL, WorkspacePolicy.readWrite(), false));
    }

    @Override
    public Workspace workspace() {
        return workspace;
    }

    @Override
    public ResourceAllocator allocator() {
        return allocator;
    }

    @Override
    public ResourceQuota quota() {
        return quota;
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
