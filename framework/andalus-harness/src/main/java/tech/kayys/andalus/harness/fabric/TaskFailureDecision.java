package tech.kayys.andalus.harness.fabric;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/** Retry or dead-letter disposition selected for a failed task execution. */
public record TaskFailureDecision(Optional<Duration> retryDelay, String category, String message) {
    public TaskFailureDecision {
        retryDelay = Objects.requireNonNull(retryDelay, "retryDelay cannot be null");
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("category cannot be blank");
        }
        if (retryDelay.isPresent() && retryDelay.get().isNegative()) {
            throw new IllegalArgumentException("retry delay cannot be negative");
        }
    }

    public static TaskFailureDecision retry(Duration delay, String category, String message) {
        return new TaskFailureDecision(Optional.of(Objects.requireNonNull(delay)), category, message);
    }

    public static TaskFailureDecision deadLetter(String category, String message) {
        return new TaskFailureDecision(Optional.empty(), category, message);
    }
}
