package tech.kayys.wayang.execution.core.resource;

import tech.kayys.wayang.execution.resource.graph.ResourceEdge;
import tech.kayys.wayang.execution.resource.graph.ResourceGraph;
import tech.kayys.wayang.execution.resource.graph.ResourceGraphVersion;
import tech.kayys.wayang.execution.resource.model.ResourceId;
import tech.kayys.wayang.execution.resource.model.VirtualResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * In-memory implementation of {@link ResourceGraph}.
 */
public class DefaultResourceGraph implements ResourceGraph {

    private final String id;
    private final ResourceGraphVersion version;
    private final Map<ResourceId, VirtualResource> resources = new LinkedHashMap<>();
    private final List<ResourceEdge> edges = new ArrayList<>();

    public DefaultResourceGraph(String id, ResourceGraphVersion version) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.version = version != null ? version : ResourceGraphVersion.initial();
    }

    public DefaultResourceGraph(String id) {
        this(id, ResourceGraphVersion.initial());
    }

    public DefaultResourceGraph addResource(VirtualResource resource) {
        Objects.requireNonNull(resource, "resource cannot be null");
        resources.put(resource.id(), resource);
        return this;
    }

    public DefaultResourceGraph addEdge(ResourceEdge edge) {
        Objects.requireNonNull(edge, "edge cannot be null");
        edges.add(edge);
        return this;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public ResourceGraphVersion version() {
        return version;
    }

    @Override
    public Collection<VirtualResource> resources() {
        return Collections.unmodifiableCollection(resources.values());
    }

    @Override
    public Collection<ResourceEdge> edges() {
        return Collections.unmodifiableList(edges);
    }

    @Override
    public Optional<VirtualResource> findResource(ResourceId id) {
        return Optional.ofNullable(resources.get(id));
    }

    @Override
    public Collection<ResourceEdge> dependenciesOf(ResourceId id) {
        List<ResourceEdge> deps = new ArrayList<>();
        for (ResourceEdge edge : edges) {
            if (edge.source().equals(id)) {
                deps.add(edge);
            }
        }
        return Collections.unmodifiableList(deps);
    }
}
