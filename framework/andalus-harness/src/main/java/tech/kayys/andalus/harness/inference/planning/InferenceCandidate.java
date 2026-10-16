package tech.kayys.andalus.harness.inference.planning;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Model candidate evaluated during inference planning.
 */
public record InferenceCandidate(
        String candidateId,
        String modelId,
        String providerId,
        boolean local,
        Set<String> capabilities,
        InferenceCandidateLimits limits,
        InferenceCandidateTelemetry telemetry,
        Map<String, Object> metadata
) {
    public InferenceCandidate {
        if (candidateId == null || candidateId.isBlank()) {
            throw new IllegalArgumentException("candidateId is required");
        }
        Objects.requireNonNull(modelId, "modelId is required");
        Objects.requireNonNull(providerId, "providerId is required");
        capabilities = capabilities == null ? Set.of() : Set.copyOf(capabilities);
        limits = limits == null ? InferenceCandidateLimits.standard(8192, 2048) : limits;
        telemetry = telemetry == null ? InferenceCandidateTelemetry.empty() : telemetry;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static InferenceCandidate local(String modelId, String providerId, Set<String> capabilities) {
        return new InferenceCandidate(
                providerId + ":" + modelId,
                modelId,
                providerId,
                true,
                capabilities,
                InferenceCandidateLimits.standard(8192, 2048),
                InferenceCandidateTelemetry.empty(),
                Map.of()
        );
    }

    public static InferenceCandidate cloud(String modelId, String providerId, Set<String> capabilities) {
        return new InferenceCandidate(
                providerId + ":" + modelId,
                modelId,
                providerId,
                false,
                capabilities,
                InferenceCandidateLimits.standard(16384, 4096),
                InferenceCandidateTelemetry.empty(),
                Map.of()
        );
    }
}
