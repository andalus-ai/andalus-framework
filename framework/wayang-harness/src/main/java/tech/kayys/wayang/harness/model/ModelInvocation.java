package tech.kayys.wayang.harness.model;

import java.util.Objects;

/**
 * Represents a model invocation.
 *
 * <p>Its components capture `id`, `model id`, `input`, `parameters`.</p>
 *
 * @param id the id
 * @param modelId the model id
 * @param input the input
 * @param parameters the parameters
 */


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
