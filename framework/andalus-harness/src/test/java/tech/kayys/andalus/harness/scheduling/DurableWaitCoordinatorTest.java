package tech.kayys.andalus.harness.scheduling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.execution.state.DefaultExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionCursor;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionStatus;
import tech.kayys.andalus.harness.execution.state.InMemoryExecutionStateStore;
import tech.kayys.andalus.harness.journal.ExecutionEventType;
import tech.kayys.andalus.harness.journal.InMemoryExecutionJournal;
import tech.kayys.andalus.harness.scheduling.temporal.Deadline;
import tech.kayys.andalus.harness.scheduling.temporal.DefaultDurableWaitCoordinator;
import tech.kayys.andalus.harness.scheduling.temporal.DurableWaitCoordinator;
import tech.kayys.andalus.harness.scheduling.temporal.WakeCondition;
import tech.kayys.andalus.harness.scheduling.temporal.WakeResult;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the 6.15 DurableWaitCoordinator — durable WAITING lifecycle.
 */
class DurableWaitCoordinatorTest {

    private InMemoryExecutionStateStore stateStore;
    private InMemoryExecutionJournal journal;
    private DurableWaitCoordinator coordinator;
    private ExecutionId executionId;

    @BeforeEach
    void setUp() {
        stateStore = new InMemoryExecutionStateStore();
        journal = new InMemoryExecutionJournal();
        coordinator = new DefaultDurableWaitCoordinator(stateStore, journal);
        executionId = ExecutionId.of("exec-wait-001");

        // Start in RUNNING state
        DefaultExecutionState running = new DefaultExecutionState(
                executionId, ExecutionStatus.RUNNING, 1L, null, ExecutionCursor.initial(), null);
        stateStore.save(running, 0L);
    }

    @Test
    void enterWaiting_transitionsToWaiting() {
        coordinator.enterWaiting(executionId,
                new WakeCondition.HumanWake("approval-123"),
                Optional.empty());

        var state = stateStore.load(executionId);
        assertTrue(state.isPresent());
        assertEquals(ExecutionStatus.WAITING, state.get().status());
    }

    @Test
    void enterWaiting_journalsWaitingEvent() {
        coordinator.enterWaiting(executionId,
                new WakeCondition.TimeWake(Instant.now().plusSeconds(3600)),
                Optional.empty());

        tech.kayys.andalus.harness.consistency.state.ExecutionId journalId =
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId.value());

        boolean hasWaitingEvent = journal.history(journalId).stream()
                .anyMatch(e -> e.type() == ExecutionEventType.EXECUTION_WAITING);
        assertTrue(hasWaitingEvent, "EXECUTION_WAITING event must be appended to journal");
    }

    @Test
    void enterWaiting_alreadyExpiredDeadline_transitionsToTimeout() {
        Deadline expiredDeadline = new Deadline(Instant.now().minusSeconds(60));

        coordinator.enterWaiting(executionId,
                new WakeCondition.HumanWake("approval-old"),
                Optional.of(expiredDeadline));

        var state = stateStore.load(executionId);
        assertTrue(state.isPresent());
        assertEquals(ExecutionStatus.TIMEOUT, state.get().status(),
                "Already-expired deadline at enterWaiting time → TIMEOUT, not WAITING");
    }

    @Test
    void enterWaiting_idempotent_doubleCallIgnored() {
        coordinator.enterWaiting(executionId,
                new WakeCondition.HumanWake("approval-456"),
                Optional.empty());

        // Should not throw or corrupt state
        coordinator.enterWaiting(executionId,
                new WakeCondition.HumanWake("approval-456"),
                Optional.empty());

        assertEquals(ExecutionStatus.WAITING, stateStore.load(executionId).get().status());
    }

    @Test
    void wakeExecution_fromWaiting_transitionsToRunning() {
        coordinator.enterWaiting(executionId,
                new WakeCondition.HumanWake("approval-789"),
                Optional.empty());

        WakeResult result = coordinator.wakeExecution(executionId);

        assertEquals(WakeResult.WOKEN, result);
        assertEquals(ExecutionStatus.RUNNING, stateStore.load(executionId).get().status());
    }

    @Test
    void wakeExecution_journalsResumingEvent() {
        coordinator.enterWaiting(executionId,
                new WakeCondition.HumanWake("approval-111"),
                Optional.empty());
        coordinator.wakeExecution(executionId);

        tech.kayys.andalus.harness.consistency.state.ExecutionId journalId =
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId.value());

        boolean hasResumingEvent = journal.history(journalId).stream()
                .anyMatch(e -> e.type() == ExecutionEventType.EXECUTION_RESUMING);
        assertTrue(hasResumingEvent, "EXECUTION_RESUMING must be journaled after wake");
    }

    @Test
    void wakeExecution_deadlineExceeded_transitionsToTimeout() {
        // Enter WAITING with deadline 1 second from now
        Deadline nearDeadline = new Deadline(Instant.now().plusSeconds(1));
        coordinator.enterWaiting(executionId,
                new WakeCondition.HumanWake("approval-222"),
                Optional.of(nearDeadline));

        // Simulate time passing beyond deadline by injecting an already-expired deadline
        // We need to test the timeout path — use a coordinator that sees the deadline has passed
        // For this test we create a fresh execution with an already-expired deadline stored in journal
        ExecutionId expiredId = ExecutionId.of("exec-wait-expired");
        DefaultExecutionState waiting = new DefaultExecutionState(
                expiredId, ExecutionStatus.WAITING, 2L, null, ExecutionCursor.initial(), null);
        stateStore.save(waiting, 0L);

        // Manually append EXECUTION_WAITING with past deadline in payload
        tech.kayys.andalus.harness.consistency.state.ExecutionId jId =
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(expiredId.value());
        var waitEvent = new tech.kayys.andalus.harness.journal.DefaultExecutionEvent(
                tech.kayys.andalus.harness.journal.EventId.generate(),
                jId,
                tech.kayys.andalus.harness.journal.EventSequence.of(1),
                Instant.now(),
                ExecutionEventType.EXECUTION_WAITING,
                Optional.empty(),
                java.util.Map.of("deadlineAt", Instant.now().minusSeconds(60).toString())
        );
        journal.append(waitEvent);

        WakeResult result = coordinator.wakeExecution(expiredId);
        assertEquals(WakeResult.EXPIRED, result);
        assertEquals(ExecutionStatus.TIMEOUT, stateStore.load(expiredId).get().status());
    }

    @Test
    void wakeExecution_terminalExecution_isIgnored() {
        DefaultExecutionState completed = new DefaultExecutionState(
                executionId, ExecutionStatus.COMPLETED, 5L, null, ExecutionCursor.initial(), null);
        stateStore.save(completed, 1L);

        WakeResult result = coordinator.wakeExecution(executionId);
        assertEquals(WakeResult.IGNORED, result, "Terminal executions must not be woken");
    }

    @Test
    void wakeExecution_notFound_returnsNotFound() {
        WakeResult result = coordinator.wakeExecution(ExecutionId.of("nonexistent"));
        assertEquals(WakeResult.NOT_FOUND, result);
    }

    @Test
    void wakeExecution_notWaiting_isIgnored() {
        // State is RUNNING, not WAITING
        WakeResult result = coordinator.wakeExecution(executionId);
        assertEquals(WakeResult.IGNORED, result, "Non-WAITING execution wake is idempotent no-op");
    }
}
