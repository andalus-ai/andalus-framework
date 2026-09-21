package tech.kayys.andalus.harness.model;

import java.util.Collection;
import java.util.Optional;

/**
 * Defines the contract for model provider operations in the Andalus framework.
 */


public interface ModelProvider {

    Collection<ModelDescriptor> models();

    Optional<ModelDescriptor> describe(ModelId modelId);

    ModelExecutor executor(ModelId modelId);
}
