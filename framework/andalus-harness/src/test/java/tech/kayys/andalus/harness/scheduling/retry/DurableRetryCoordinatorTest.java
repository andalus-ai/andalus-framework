package tech.kayys.andalus.harness.scheduling.retry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.attempt.ExecutionAttempt;
import tech.kayys.andalus.harness.context.DefaultHarnessContext;
import tech.kayys.andalus.harness.context.DefaultHarnessIdentity;
import tech.kayys.andalus.harness.context.DefaultHarnessSession;
import tech.kayys.andalus.harness.execution.checkpoint.InMemoryCheckpointManager;
import tech.kayys.andalus.harness.execution.state.*;
import tech.kayys.andalus.harness.journal.ExecutionEventType;
import tech.kayys.andalus.harness.journal.InMemoryExecutionJournal;
import tech.kayys.andalus.harness.scheduling.temporal.Deadline;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DurableRetryCoordinatorTest {

    private InMemoryExecutionStateStore stateStore;
    private InMemoryCheckpointManager checkpointManager;
    private InMemoryExecutionJournal journal;
    private DefaultDurableRetryCoordinator coordinator;
    private ExecutionId executionId;
    private DefaultExecutionState initialState;

    @BeforeEach
    void setUp() {
        stateStore = new InMemoryExecutionStateStore();
        checkpointManager = new InMemoryCheckpointManager();
        journal = new InMemoryExecutionJournal();
        coordinator = new DefaultDurableRetryCoordinator(stateStore, checkpointManager, journal);

        executionId = ExecutionId.of("exec-retry-1");
        DefaultHarnessContext context = new DefaultHarnessContext(
                DefaultHarnessIdentity.of("retry-agent"),
                DefaultHarnessSession.createNew(),
                Map.of()
        );
        initialState = new DefaultExecutionState(
                executionId,
                ExecutionStatus.RUNNING,
                0L,
                context,
                ExecutionCursor.initial(),
                ExecutionData.empty()
        );
        stateStore.save(initialState, 0L);
    }

    @Test
    void testRetryScheduledWithinDeadline() {
        ExecutionAttempt attempt1 = ExecutionAttempt.initial(executionId.value(), "worker-1");
        assertEquals(1, attempt1.number());

        RetryPolicy policy = RetryPolicy.maxAttempts(3, BackoffStrategy.fixed(Duration.ofSeconds(5)));
        Instant now = Instant.now();
        Optional<Deadline> deadline = Optional.of(new Deadline(now.plus(Duration.ofMinutes(10))));

        RetryResult result = coordinator.evaluateAndSchedule(
                executionId,
                attempt1,
                new IOException("Model provider timeout"),
                policy,
                deadline,
                now
        );

        assertTrue(result.isScheduled());
        assertEquals(RetryResult.RetryOutcome.SCHEDULED, result.outcome());
        assertTrue(result.nextAttempt().isPresent());

        ExecutionAttempt attempt2 = result.nextAttempt().get();
        assertEquals(2, attempt2.number());
        assertEquals(executionId.value(), attempt2.executionId());
        assertNotEquals(attempt1.attemptId(), attempt2.attemptId());

        // State transitioned to WAITING
        Optional<ExecutionState> state = stateStore.load(executionId);
        assertTrue(state.isPresent());
        assertEquals(ExecutionStatus.WAITING, state.get().status());

        // Journal event recorded
        tech.kayys.andalus.harness.consistency.state.ExecutionId jId =
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId.value());
        assertTrue(journal.history(jId).stream().anyMatch(e -> e.type() == ExecutionEventType.EXECUTION_RETRY_SCHEDULED));
    }

    @Test
    void testRetryExceedingMaxAttemptsTerminates() {
        ExecutionAttempt attempt1 = ExecutionAttempt.initial(executionId.value(), "worker-1");
        ExecutionAttempt attempt2 = attempt1.next("worker-1");
        ExecutionAttempt attempt3 = attempt2.next("worker-1");
        assertEquals(3, attempt3.number());

        RetryPolicy policy = RetryPolicy.maxAttempts(3, BackoffStrategy.fixed(Duration.ofSeconds(5)));
        Instant now = Instant.now();

        RetryResult result = coordinator.evaluateAndSchedule(
                executionId,
                attempt3,
                new RuntimeException("Repeated model failure"),
                policy,
                Optional.empty(),
                now
        );

        assertTrue(result.isTerminated());
        assertEquals(RetryResult.RetryOutcome.TERMINATED, result.outcome());
        assertTrue(result.nextAttempt().isEmpty());

        // State transitioned to FAILED
        Optional<ExecutionState> state = stateStore.load(executionId);
        assertTrue(state.isPresent());
        assertEquals(ExecutionStatus.FAILED, state.get().status());

        // Journal event recorded
        tech.kayys.andalus.harness.consistency.state.ExecutionId jId =
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId.value());
        assertTrue(journal.history(jId).stream().anyMatch(e -> e.type() == ExecutionEventType.EXECUTION_FAILED));
    }

    @Test
    void testRetryPastDeadlineTimesOut() {
        ExecutionAttempt attempt1 = ExecutionAttempt.initial(executionId.value(), "worker-1");

        // 30-second delay, but deadline expires in 10 seconds
        RetryPolicy policy = RetryPolicy.maxAttempts(3, BackoffStrategy.fixed(Duration.ofSeconds(30)));
        Instant now = Instant.now();
        Optional<Deadline> deadline = Optional.of(new Deadline(now.plus(Duration.ofSeconds(10))));

        RetryResult result = coordinator.evaluateAndSchedule(
                executionId,
                attempt1,
                new IOException("Downstream error"),
                policy,
                deadline,
                now
        );

        assertTrue(result.isTimedOut());
        assertEquals(RetryResult.RetryOutcome.TIMED_OUT, result.outcome());
        assertTrue(result.reason().contains("exceeds execution deadline"));

        // State transitioned to TIMEOUT
        Optional<ExecutionState> state = stateStore.load(executionId);
        assertTrue(state.isPresent());
        assertEquals(ExecutionStatus.TIMEOUT, state.get().status());

        // Journal event recorded
        tech.kayys.andalus.harness.consistency.state.ExecutionId jId =
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId.value());
        assertTrue(journal.history(jId).stream().anyMatch(e -> e.type() == ExecutionEventType.EXECUTION_EXPIRED));
    }

    @Test
    void testNoRetryConfiguredTerminates() {
        ExecutionAttempt attempt1 = ExecutionAttempt.initial(executionId.value(), "worker-1");
        Instant now = Instant.now();

        RetryResult result = coordinator.evaluateAndSchedule(
                executionId,
                attempt1,
                new IllegalArgumentException("Fatal user bad request"),
                RetryPolicy.none(),
                Optional.empty(),
                now
        );

        assertTrue(result.isTerminated());
        assertEquals(ExecutionStatus.FAILED, stateStore.load(executionId).get().status());
    }
}
