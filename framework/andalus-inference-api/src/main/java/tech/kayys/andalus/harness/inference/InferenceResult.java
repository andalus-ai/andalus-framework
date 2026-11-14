package tech.kayys.andalus.harness.inference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Result returned from an inference execution.
 */
public record InferenceResult(
        InferenceStatus status,
        Object output,
        InferenceUsage usage,
        Optional<String> modelId,
        Optional<String> providerId,
        Map<String, Object> metadata
) {
    public InferenceResult {
        Objects.requireNonNull(status, "status is required");
        usage = usage == null ? InferenceUsage.empty() : usage;
        modelId = modelId == null ? Optional.empty() : modelId;
        providerId = providerId == null ? Optional.empty() : providerId;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static InferenceResult completed(String modelId, Object output, InferenceUsage usage) {
        return new InferenceResult(
                InferenceStatus.COMPLETED,
                output,
                usage,
                Optional.ofNullable(modelId),
                Optional.empty(),
                Map.of()
        );
    }

    public static InferenceResult completed(String modelId, String providerId, Object output, InferenceUsage usage) {
        return new InferenceResult(
                InferenceStatus.COMPLETED,
                output,
                usage,
                Optional.ofNullable(modelId),
                Optional.ofNullable(providerId),
                Map.of()
        );
    }

    public static InferenceResult failed(InferenceStatus status, String errorMessage) {
        return new InferenceResult(
                status,
                null,
                InferenceUsage.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of("error", errorMessage != null ? errorMessage : "")
        );
    }

    public boolean isSuccess() {
        return status == InferenceStatus.COMPLETED;
    }
}
