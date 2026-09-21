package tech.kayys.andalus.execution.resource.provider;

import tech.kayys.andalus.execution.resource.binding.ResourceBindings;
import tech.kayys.andalus.execution.resource.model.VirtualResource;

import java.util.Collection;

/**
 * Orchestrates multi-resource provisioning across registered providers.
 */
public interface ResourceProvisioner {

    ResourceBindings provisionAll(Collection<VirtualResource> resources, ResourceContext context);

    void releaseAll(ResourceBindings bindings, ResourceContext context);
}
