package tech.kayys.wayang.harness.model;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultModelCatalog implements ModelCatalog {

    private final List<ModelProvider> providers = new CopyOnWriteArrayList<>();

    @Override
    public Collection<ModelDescriptor> discover(ModelTask task) {
        List<ModelDescriptor> result = new ArrayList<>();
        for (ModelProvider provider : providers) {
            for (ModelDescriptor desc : provider.models()) {
                if (task == null || desc.tasks().contains(task)) {
                    result.add(desc);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public Collection<ModelDescriptor> all() {
        return discover(null);
    }

    @Override
    public void register(ModelProvider provider) {
        if (provider != null && !providers.contains(provider)) {
            providers.add(provider);
        }
    }

    @Override
    public void unregister(ModelProvider provider) {
        if (provider != null) {
            providers.remove(provider);
        }
    }

    @Override
    public Optional<ModelProvider> providerFor(ModelId modelId) {
        if (modelId == null) return Optional.empty();
        for (ModelProvider provider : providers) {
            if (provider.describe(modelId).isPresent()) {
                return Optional.of(provider);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ModelDescriptor> get(ModelId modelId) {
        if (modelId == null) return Optional.empty();
        return providerFor(modelId).flatMap(p -> p.describe(modelId));
    }
}
