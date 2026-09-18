package tech.kayys.wayang.harness.model;

import java.util.Objects;

public record ModelInvocation(
        ModelInvocationId id,
        ModelId modelId,
        ModelInput input,
        GenerationParameters parameters
) {
    public ModelInvocation {
        Objects.requireNonNull(id, "ModelInvocationId cannot be null");
        Objects.requireNonNull(modelId, "ModelId cannot be null");
        Objects.requireNonNull(input, "ModelInput cannot be null");
        if (parameters == null) parameters = GenerationParameters.defaults();
    }

    public static ModelInvocation of(ModelId modelId, ModelInput input) {
        return new ModelInvocation(ModelInvocationId.generate(), modelId, input, GenerationParameters.defaults());
    }
}
