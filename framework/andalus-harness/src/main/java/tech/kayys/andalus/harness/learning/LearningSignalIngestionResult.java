package tech.kayys.andalus.harness.learning;

import java.util.Map;
import java.util.Objects;

/**
 * Outcome of ingesting a {@link LearningSignal}.
 */
public record LearningSignalIngestionResult(
        String signalId,
        LearningSignalIngestionStatus status,
        String message,
        Map<String, Object> diagnostics
) {
    public LearningSignalIngestionResult {
        Objects.requireNonNull(signalId, "signalId is required");
        Objects.requireNonNull(status, "status is required");
        message = message == null ? "" : message;
        diagnostics = diagnostics == null ? Map.of() : Map.copyOf(diagnostics);
    }

    public static LearningSignalIngestionResult accepted(String signalId) {
        return new LearningSignalIngestionResult(signalId, LearningSignalIngestionStatus.ACCEPTED, "Signal accepted", Map.of());
    }

    public static LearningSignalIngestionResult duplicate(String signalId) {
        return new LearningSignalIngestionResult(signalId, LearningSignalIngestionStatus.DUPLICATE, "Duplicate signal detected", Map.of());
    }

    public static LearningSignalIngestionResult rejected(String signalId, String reason) {
        return new LearningSignalIngestionResult(signalId, LearningSignalIngestionStatus.REJECTED, reason, Map.of("reason", reason != null ? reason : ""));
    }

    public static LearningSignalIngestionResult failed(String signalId, String errorMessage) {
        return new LearningSignalIngestionResult(signalId, LearningSignalIngestionStatus.FAILED, errorMessage, Map.of("error", errorMessage != null ? errorMessage : ""));
    }

    public boolean isSuccess() {
        return status.isSuccess();
    }
}
