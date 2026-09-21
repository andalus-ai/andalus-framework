package tech.kayys.andalus.execution.resource.graph;

import tech.kayys.andalus.execution.resource.model.ResourceId;

import java.util.Objects;

/**
 * Directed edge representing a dependency or mounting relationship between virtual resources.
 */
public record ResourceEdge(
        ResourceId source,
        ResourceId target,
        ResourceRelation relation
) {
    public ResourceEdge {
        Objects.requireNonNull(source, "source cannot be null");
        Objects.requireNonNull(target, "target cannot be null");
        Objects.requireNonNull(relation, "relation cannot be null");
    }

    public static ResourceEdge of(ResourceId source, ResourceId target, ResourceRelation relation) {
        return new ResourceEdge(source, target, relation);
    }
}
