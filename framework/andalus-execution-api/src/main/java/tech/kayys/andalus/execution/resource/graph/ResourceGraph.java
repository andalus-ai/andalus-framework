package tech.kayys.andalus.execution.resource.graph;

import tech.kayys.andalus.execution.resource.model.ResourceId;
import tech.kayys.andalus.execution.resource.model.VirtualResource;

import java.util.Collection;
import java.util.Optional;

/**
 * Virtual Resource Graph composing logical resources and their dependencies.
 */
public interface ResourceGraph {

    String id();

    ResourceGraphVersion version();

    Collection<VirtualResource> resources();

    Collection<ResourceEdge> edges();

    Optional<VirtualResource> findResource(ResourceId id);

    Collection<ResourceEdge> dependenciesOf(ResourceId id);
}
