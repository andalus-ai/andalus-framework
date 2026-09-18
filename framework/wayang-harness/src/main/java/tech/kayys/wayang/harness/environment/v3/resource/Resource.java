package tech.kayys.wayang.harness.environment.v3.resource;

/**
 * Universal interface for an individual managed resource.
 */
public interface Resource {

    ResourceId id();

    ResourceType type();

    ResourceDescriptor descriptor();

    ResourceState state();
}
