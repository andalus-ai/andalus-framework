package tech.kayys.andalus.harness.protocol;

import tech.kayys.andalus.harness.model.ModelDescriptor;
import tech.kayys.andalus.harness.model.ModelId;
import tech.kayys.andalus.harness.model.ModelTask;

import java.util.Collection;
import java.util.Optional;

/**
 * Defines the contract for model catalog view operations in the Andalus framework.
 */


public interface ModelCatalogView {

    Collection<ModelDescriptor> availableModels();

    Collection<ModelDescriptor> modelsForTask(ModelTask task);

    Optional<ModelDescriptor> find(ModelId modelId);
}
