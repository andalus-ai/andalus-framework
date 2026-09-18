package tech.kayys.wayang.harness.model;

import java.util.Collection;
import java.util.Optional;

public interface ModelCatalog {

    Collection<ModelDescriptor> discover(ModelTask task);

    Collection<ModelDescriptor> all();

    void register(ModelProvider provider);

    void unregister(ModelProvider provider);

    Optional<ModelProvider> providerFor(ModelId modelId);

    Optional<ModelDescriptor> get(ModelId modelId);

    static ModelCatalog create() {
        return new DefaultModelCatalog();
    }
}
