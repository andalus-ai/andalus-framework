package tech.kayys.wayang.execution.resource.provider;

import tech.kayys.wayang.execution.resource.binding.ResourceBinding;
import tech.kayys.wayang.execution.resource.model.ResourceType;
import tech.kayys.wayang.execution.resource.model.VirtualResource;

/**
 * SPI for infrastructure providers that provision and bind physical resources to virtual requests.
 */
public interface ResourceProvider {

    ResourceType type();

    boolean supports(VirtualResource resource);

    ResourceBinding provision(VirtualResource resource, ResourceContext context);

    void release(ResourceBinding binding, ResourceContext context);
}
