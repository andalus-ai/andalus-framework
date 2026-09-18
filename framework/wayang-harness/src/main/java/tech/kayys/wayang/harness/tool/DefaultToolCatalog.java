package tech.kayys.wayang.harness.tool;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultToolCatalog implements ToolCatalog {

    private final List<ToolProvider> providers = new CopyOnWriteArrayList<>();

    @Override
    public Collection<ToolDescriptor> discover(ToolDiscoveryRequest request) {
        List<ToolDescriptor> result = new ArrayList<>();
        Set<String> requestedCaps = request != null ? request.capabilities() : Set.of();

        for (ToolProvider provider : providers) {
            for (ToolDescriptor desc : provider.tools()) {
                if (requestedCaps.isEmpty() || desc.capabilities().stream().anyMatch(requestedCaps::contains)) {
                    result.add(desc);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public void register(ToolProvider provider) {
        if (provider != null && !providers.contains(provider)) {
            providers.add(provider);
        }
    }

    @Override
    public void unregister(ToolProvider provider) {
        if (provider != null) {
            providers.remove(provider);
        }
    }

    @Override
    public Optional<ToolProvider> providerFor(ToolId toolId) {
        if (toolId == null) {
            return Optional.empty();
        }
        for (ToolProvider provider : providers) {
            if (provider.describe(toolId).isPresent()) {
                return Optional.of(provider);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ToolDescriptor> get(ToolId toolId) {
        if (toolId == null) {
            return Optional.empty();
        }
        return providerFor(toolId).flatMap(p -> p.describe(toolId));
    }
}
