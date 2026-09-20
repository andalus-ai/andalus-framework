package tech.kayys.wayang.execution.resource.model;

import java.util.Map;

/**
 * Logical virtual resource required by an execution.
 */
public interface VirtualResource {

    ResourceId id();

    ResourceType type();

    ResourceScope scope();

    ResourceLifetime lifetime();

    Map<String, Object> attributes();
}
