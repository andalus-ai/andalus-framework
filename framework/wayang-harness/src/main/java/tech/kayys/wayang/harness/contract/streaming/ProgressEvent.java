package tech.kayys.wayang.harness.contract.streaming;

import java.time.Instant;

/**
 * Distinct progress event (progress is not result).
 */
public record ProgressEvent(
        String taskId,
        int currentStep,
        int totalSteps,
        String message,
        Instant timestamp
) {
    public static ProgressEvent of(String taskId, int step, int total, String msg) {
        return new ProgressEvent(taskId, step, total, msg, Instant.now());
    }
}
