package tech.kayys.wayang.execution.mobility.state;

import java.util.Map;
import java.util.Objects;

/**
 * Result of reconstructing execution runtime state from an envelope.
 */
public record ReconstructionResult(
        boolean success,
        String executionId,
        Map<String, Object> reconstructedMetadata,
        String errorMessage
) {
    public ReconstructionResult {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        reconstructedMetadata = reconstructedMetadata == null ? Map.of() : Map.copyOf(reconstructedMetadata);
    }

    public static ReconstructionResult success(String executionId, Map<String, Object> metadata) {
        return new ReconstructionResult(true, executionId, metadata, null);
    }

    public static ReconstructionResult failure(String executionId, String error) {
        return new ReconstructionResult(false, executionId, Map.of(), error);
    }
}
