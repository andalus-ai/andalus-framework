package tech.kayys.wayang.execution.resource.provider;

import tech.kayys.wayang.execution.resource.binding.ResourceBindings;
import tech.kayys.wayang.execution.resource.model.VirtualResource;

import java.util.Collection;

/**
 * Orchestrates multi-resource provisioning across registered providers.
 */
public interface ResourceProvisioner {

    ResourceBindings provisionAll(Collection<VirtualResource> resources, ResourceContext context);

    void releaseAll(ResourceBindings bindings, ResourceContext context);
}
