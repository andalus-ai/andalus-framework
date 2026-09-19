package tech.kayys.wayang.execution.attempt;

import java.util.Objects;
import java.util.Optional;

public record AttemptLineage(
        String executionId,
        AttemptId attemptId,
        Optional<AttemptId> parentAttemptId,
        Optional<String> restoredFromCheckpointId
) {
    public AttemptLineage {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(attemptId, "attemptId cannot be null");
    }
}
