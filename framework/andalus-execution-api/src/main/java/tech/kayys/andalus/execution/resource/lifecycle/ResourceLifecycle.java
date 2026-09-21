package tech.kayys.andalus.execution.resource.lifecycle;

import tech.kayys.andalus.execution.resource.model.ResourceId;

/**
 * Manages resource activation, health checking, and deprovisioning.
 */
public interface ResourceLifecycle {

    void activate(ResourceId id);

    void release(ResourceId id);

    ResourceHealth checkHealth(ResourceId id);
}
