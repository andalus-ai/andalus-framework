package tech.kayys.wayang.execution.attempt;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Concrete physical attempt of a logical execution.
 */
public record ExecutionAttempt(
        String executionId,
        AttemptId attemptId,
        int number,
        AttemptState state,
        String targetWorkerId,
        Instant startedAt,
        Optional<Instant> finishedAt
) {
    public ExecutionAttempt {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(attemptId, "attemptId cannot be null");
        Objects.requireNonNull(state, "state cannot be null");
        if (number <= 0) {
            throw new IllegalArgumentException("attempt number must be > 0");
        }
    }

    public static ExecutionAttempt initial(String executionId, String targetWorkerId) {
        return new ExecutionAttempt(executionId, AttemptId.random(), 1, AttemptState.INITIALIZING, targetWorkerId, Instant.now(), Optional.empty());
    }

    public ExecutionAttempt next(String targetWorkerId) {
        return new ExecutionAttempt(executionId, AttemptId.random(), number + 1, AttemptState.INITIALIZING, targetWorkerId, Instant.now(), Optional.empty());
    }

    public ExecutionAttempt withState(AttemptState newState) {
        Optional<Instant> finished = (newState == AttemptState.COMPLETED || newState == AttemptState.FAILED || newState == AttemptState.LOST)
                ? Optional.of(Instant.now())
                : finishedAt;
        return new ExecutionAttempt(executionId, attemptId, number, newState, targetWorkerId, startedAt, finished);
    }
}
