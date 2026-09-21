package tech.kayys.andalus.harness.scheduling.retry;

import tech.kayys.andalus.execution.attempt.ExecutionAttempt;
import tech.kayys.andalus.harness.execution.checkpoint.CheckpointManager;
import tech.kayys.andalus.harness.execution.state.DefaultExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionStateStore;
import tech.kayys.andalus.harness.execution.state.ExecutionStatus;
import tech.kayys.andalus.harness.journal.DefaultExecutionEvent;
import tech.kayys.andalus.harness.journal.EventSequence;
import tech.kayys.andalus.harness.journal.ExecutionEventType;
import tech.kayys.andalus.harness.journal.ExecutionJournal;
import tech.kayys.andalus.harness.scheduling.temporal.Deadline;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of {@link DurableRetryCoordinator}.
 *
 * <p>Enforces that the logical execution identity remains constant across retry attempts,
 * prevents retry scheduling past hard deadlines, transitions execution state durably,
 * and emits audit journal events.</p>
 */
public class DefaultDurableRetryCoordinator implements DurableRetryCoordinator {

    private final ExecutionStateStore stateStore;
    private final CheckpointManager checkpointManager;
    private final ExecutionJournal journal;

    public DefaultDurableRetryCoordinator(
            ExecutionStateStore stateStore,
            CheckpointManager checkpointManager
    ) {
        this(stateStore, checkpointManager, null);
    }

    public DefaultDurableRetryCoordinator(
            ExecutionStateStore stateStore,
            CheckpointManager checkpointManager,
            ExecutionJournal journal
    ) {
        this.stateStore = Objects.requireNonNull(stateStore, "stateStore");
        this.checkpointManager = Objects.requireNonNull(checkpointManager, "checkpointManager");
        this.journal = journal;
    }

    @Override
    public RetryResult evaluateAndSchedule(
            ExecutionId executionId,
            ExecutionAttempt currentAttempt,
            Throwable failure,
            RetryPolicy policy,
            Optional<Deadline> deadline,
            Instant now
    ) {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(currentAttempt, "currentAttempt");
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(now, "now");

        RetryDecision decision = policy.evaluate(failure, currentAttempt.number());

        if (decision.shouldRetry()) {
            Instant retryAt = now.plus(decision.delay());

            // Check if retry is bounded by hard deadline
            if (deadline.isPresent() && deadline.get().isExceeded(retryAt)) {
                // Deadline would be exceeded before or at retry time
                ExecutionState current = loadRequired(executionId);
                ExecutionState timedOutState = transitionStatus(current, ExecutionStatus.TIMEOUT);
                stateStore.save(timedOutState, current.version());

                recordEvent(executionId, ExecutionEventType.EXECUTION_EXPIRED, Map.of(
                        "deadline", deadline.get().at().toString(),
                        "retryAttemptedAt", retryAt.toString(),
                        "reason", "Retry scheduled beyond execution deadline"
                ));

                return RetryResult.timedOut("Retry attempt scheduled at " + retryAt +
                        " exceeds execution deadline " + deadline.get().at());
            }

            // Valid retry within bounds
            ExecutionState current = loadRequired(executionId);
            ExecutionState waitingState = transitionStatus(current, ExecutionStatus.WAITING);
            stateStore.save(waitingState, current.version());

            ExecutionAttempt nextAttempt = currentAttempt.next(currentAttempt.targetWorkerId());

            recordEvent(executionId, ExecutionEventType.EXECUTION_RETRY_SCHEDULED, Map.of(
                    "attempt", nextAttempt.number(),
                    "attemptId", nextAttempt.attemptId().value(),
                    "previousAttemptId", currentAttempt.attemptId().value(),
                    "delay", decision.delay().toString(),
                    "scheduledAt", retryAt.toString(),
                    "reason", decision.reason() != null ? decision.reason() : ""
            ));

            return RetryResult.scheduled(nextAttempt, retryAt, decision.reason());
        }

        // Policy determined non-retryable or max attempts exhausted
        ExecutionState current = loadRequired(executionId);
        ExecutionState failedState = transitionStatus(current, ExecutionStatus.FAILED);
        stateStore.save(failedState, current.version());

        recordEvent(executionId, ExecutionEventType.EXECUTION_FAILED, Map.of(
                "attempt", currentAttempt.number(),
                "reason", decision.reason() != null ? decision.reason() : ""
        ));

        return RetryResult.terminated(decision.reason());
    }

    private ExecutionState loadRequired(ExecutionId executionId) {
        return stateStore.load(executionId)
                .orElseThrow(() -> new IllegalStateException("Execution state not found for id: " + executionId.value()));
    }

    private ExecutionState transitionStatus(ExecutionState state, ExecutionStatus newStatus) {
        if (state instanceof DefaultExecutionState def) {
            return def.withStatus(newStatus);
        }
        return new DefaultExecutionState(
                state.executionId(),
                newStatus,
                state.version() + 1,
                state.context(),
                state.cursor(),
                state.data()
        );
    }

    private void recordEvent(ExecutionId executionId, ExecutionEventType type, Map<String, Object> payload) {
        if (journal != null) {
            tech.kayys.andalus.harness.consistency.state.ExecutionId jId =
                    tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId.value());
            journal.append(new DefaultExecutionEvent(
                    tech.kayys.andalus.harness.journal.EventId.generate(),
                    jId,
                    EventSequence.initial(),
                    Instant.now(),
                    type,
                    Optional.empty(),
                    payload
            ));
        }
    }
}
