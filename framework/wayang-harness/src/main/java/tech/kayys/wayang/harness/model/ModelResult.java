package tech.kayys.wayang.harness.model;

import java.util.Objects;

/**
 * Represents a model result.
 *
 * <p>Its components capture `invocation id`, `status`, `output`, `usage`, `metadata`.</p>
 *
 * @param invocationId the invocation id
 * @param status the status
 * @param output the output
 * @param usage the usage
 * @param metadata the metadata
 */


public record ModelResult(
        ModelInvocationId invocationId,
        ModelResultStatus status,
        ModelOutput output,
        ModelUsage usage,
        ModelMetadata metadata
) {
    public ModelResult {
        Objects.requireNonNull(invocationId, "invocationId cannot be null");
        Objects.requireNonNull(status, "status cannot be null");
        if (output == null) output = ModelOutput.text("");
        if (usage == null) usage = ModelUsage.empty();
        if (metadata == null) metadata = ModelMetadata.cloud("generic");
    }

    public boolean isSuccess() {
        return status == ModelResultStatus.SUCCESS;
    }

    public static ModelResult success(ModelInvocationId id, String text, ModelUsage usage, ModelMetadata metadata) {
        return new ModelResult(id, ModelResultStatus.SUCCESS, ModelOutput.text(text), usage, metadata);
    }

    public static ModelResult failure(ModelInvocationId id, String error, ModelMetadata metadata) {
        return new ModelResult(id, ModelResultStatus.FAILURE, ModelOutput.text(error), ModelUsage.empty(), metadata);
    }

    public static ModelResult denied(ModelInvocationId id, String reason) {
        return new ModelResult(id, ModelResultStatus.DENIED, ModelOutput.text(reason), ModelUsage.empty(), ModelMetadata.cloud("governance"));
    }
}
