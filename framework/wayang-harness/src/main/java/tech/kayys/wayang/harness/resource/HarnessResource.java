package tech.kayys.wayang.harness.resource;

import tech.kayys.wayang.harness.environment.ResourceId;

/**
 * An environmental resource that may be allocated, bound, or accessed by an agent execution.
 */
public interface HarnessResource {

    ResourceId id();

    ResourceType type();

    ResourceStatus status();

    ResourceMetadata metadata();
}
