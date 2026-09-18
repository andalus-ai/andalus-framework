package tech.kayys.wayang.harness.environment.v3.resource;

import java.util.Collection;
import java.util.Optional;

/**
 * Catalog of discoverable resources in the environment.
 */
public interface ResourceCatalog {

    Collection<ResourceDescriptor> available();

    Optional<ResourceDescriptor> find(ResourceId id);

    Collection<ResourceDescriptor> findByType(ResourceType type);
}
