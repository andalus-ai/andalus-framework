package tech.kayys.andalus.execution.core.resource;

import tech.kayys.andalus.execution.resource.binding.ResourceBinding;
import tech.kayys.andalus.execution.resource.binding.ResourceBindingState;
import tech.kayys.andalus.execution.resource.binding.ResourceBindings;
import tech.kayys.andalus.execution.resource.model.ResourceId;
import tech.kayys.andalus.execution.resource.model.ResourceType;
import tech.kayys.andalus.execution.resource.model.VirtualResource;
import tech.kayys.andalus.execution.resource.provider.ResourceContext;
import tech.kayys.andalus.execution.resource.provider.ResourceProvider;
import tech.kayys.andalus.execution.resource.provider.ResourceProvisioner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Standard implementation of {@link ResourceProvisioner} delegating to registered providers.
 */
public class DefaultResourceProvisioner implements ResourceProvisioner {

    private final List<ResourceProvider> providers = new CopyOnWriteArrayList<>();

    public DefaultResourceProvisioner registerProvider(ResourceProvider provider) {
        Objects.requireNonNull(provider, "provider cannot be null");
        providers.add(provider);
        return this;
    }

    @Override
    public ResourceBindings provisionAll(Collection<VirtualResource> resources, ResourceContext context) {
        Objects.requireNonNull(resources, "resources cannot be null");
        Objects.requireNonNull(context, "context cannot be null");

        Map<ResourceId, ResourceBinding> resultMap = new HashMap<>();

        for (VirtualResource res : resources) {
            ResourceProvider matched = null;
            for (ResourceProvider p : providers) {
                if (p.type() == res.type() && p.supports(res)) {
                    matched = p;
                    break;
                }
            }

            ResourceBinding binding;
            if (matched != null) {
                binding = matched.provision(res, context);
            } else {
                // Fallback virtual mock binding
                binding = ResourceBinding.active(
                        res.id(),
                        "mock-phys-" + res.id().value(),
                        Map.of("provider", "fallback-default", "type", res.type().name())
                );
            }
            resultMap.put(res.id(), binding);
        }

        return ResourceBindings.of(resultMap);
    }

    @Override
    public void releaseAll(ResourceBindings bindings, ResourceContext context) {
        if (bindings == null || context == null) {
            return;
        }

        for (ResourceBinding b : bindings.bindings().values()) {
            for (ResourceProvider p : providers) {
                p.release(b, context);
            }
        }
    }
}
