package tech.kayys.wayang.harness.model;

import java.util.Collection;
import java.util.Optional;

public interface ModelProvider {

    Collection<ModelDescriptor> models();

    Optional<ModelDescriptor> describe(ModelId modelId);

    ModelExecutor executor(ModelId modelId);
}
