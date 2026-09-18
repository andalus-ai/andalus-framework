package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.harness.model.ModelDescriptor;
import tech.kayys.wayang.harness.model.ModelId;
import tech.kayys.wayang.harness.model.ModelTask;

import java.util.Collection;
import java.util.Optional;

public interface ModelCatalogView {

    Collection<ModelDescriptor> availableModels();

    Collection<ModelDescriptor> modelsForTask(ModelTask task);

    Optional<ModelDescriptor> find(ModelId modelId);
}
