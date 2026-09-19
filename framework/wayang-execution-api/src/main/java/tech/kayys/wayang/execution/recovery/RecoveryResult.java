package tech.kayys.wayang.execution.recovery;

import java.time.Instant;
import java.util.Optional;

public record RecoveryResult(
        String executionId,
        boolean success,
        RecoveryMode modeUsed,
        Optional<String> newAttemptId,
        Optional<String> errorMessage,
        Instant completedAt
) {
    public static RecoveryResult success(String executionId, RecoveryMode mode, String newAttemptId) {
        return new RecoveryResult(executionId, true, mode, Optional.of(newAttemptId), Optional.empty(), Instant.now());
    }

    public static RecoveryResult failure(String executionId, RecoveryMode mode, String error) {
        return new RecoveryResult(executionId, false, mode, Optional.empty(), Optional.of(error), Instant.now());
    }
}
