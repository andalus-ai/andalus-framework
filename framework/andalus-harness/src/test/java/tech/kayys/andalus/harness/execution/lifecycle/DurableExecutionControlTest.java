package tech.kayys.andalus.harness.execution.lifecycle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.context.DefaultHarnessContext;
import tech.kayys.andalus.harness.context.DefaultHarnessIdentity;
import tech.kayys.andalus.harness.context.DefaultHarnessSession;
import tech.kayys.andalus.harness.execution.checkpoint.Checkpoint;
import tech.kayys.andalus.harness.execution.checkpoint.InMemoryCheckpointManager;
import tech.kayys.andalus.harness.execution.state.*;
import tech.kayys.andalus.harness.journal.ExecutionEventType;
import tech.kayys.andalus.harness.journal.InMemoryExecutionJournal;
import tech.kayys.andalus.harness.lifecycle.DefaultHarnessLifecycle;
import tech.kayys.andalus.harness.lifecycle.HarnessExecutionStatus;
import tech.kayys.andalus.harness.runtime.DefaultHarnessExecution;
import tech.kayys.andalus.harness.scheduling.cancellation.CancellationHandle;
import tech.kayys.andalus.harness.scheduling.cancellation.CancellationMode;
import tech.kayys.andalus.harness.scheduling.cancellation.InMemoryCancellationRegistry;
import tech.kayys.andalus.harness.scheduling.temporal.Deadline;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class DurableExecutionControlTest {

    private InMemoryExecutionStateStore stateStore;
    private InMemoryCheckpointManager checkpointManager;
    private InMemoryExecutionJournal journal;
    private InMemoryCancellationRegistry cancellationRegistry;
    private DefaultDurableExecutionControl control;
    private ExecutionId executionId;
    private DefaultExecutionState initialState;

    @BeforeEach
    void setUp() {
        stateStore = new InMemoryExecutionStateStore();
        checkpointManager = new InMemoryCheckpointManager();
        journal = new InMemoryExecutionJournal();
        cancellationRegistry = new InMemoryCancellationRegistry();
        control = new DefaultDurableExecutionControl(stateStore, checkpointManager, journal, cancellationRegistry);

        executionId = ExecutionId.of("exec-test-1");
        DefaultHarnessContext context = new DefaultHarnessContext(
                DefaultHarnessIdentity.of("test-agent"),
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
    void testNormalSuspendCreatesCheckpointAndTransitionsState() {
        control.suspend(executionId);

        Optional<ExecutionState> loaded = stateStore.load(executionId);
        assertTrue(loaded.isPresent());
        assertEquals(ExecutionStatus.SUSPENDED, loaded.get().status());
        assertEquals(1L, loaded.get().version());

        // Checkpoint boundary captured
        Optional<Checkpoint> latestCp = checkpointManager.latest(executionId);
        assertTrue(latestCp.isPresent());
        assertEquals(executionId, latestCp.get().executionId());

        // Journal event recorded
        tech.kayys.andalus.harness.consistency.state.ExecutionId jId =
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId.value());
        assertTrue(journal.history(jId).stream().anyMatch(e -> e.type() == ExecutionEventType.EXECUTION_SUSPENDED));
    }

    @Test
    void testSuspendIsIdempotentWhenAlreadySuspended() {
        control.suspend(executionId);
        assertDoesNotThrow(() -> control.suspend(executionId));

        Optional<ExecutionState> loaded = stateStore.load(executionId);
        assertTrue(loaded.isPresent());
        assertEquals(ExecutionStatus.SUSPENDED, loaded.get().status());
    }

    @Test
    void testSuspendRejectsTerminalExecution() {
        DefaultExecutionState completed = initialState.withStatus(ExecutionStatus.COMPLETED);
        stateStore.save(completed, 0L);

        assertThrows(IllegalStateException.class, () -> control.suspend(executionId));
    }

    @Test
    void testNormalResumeRestoresCheckpointAndTransitionsState() {
        control.suspend(executionId);
        control.resume(executionId);

        Optional<ExecutionState> loaded = stateStore.load(executionId);
        assertTrue(loaded.isPresent());
        assertEquals(ExecutionStatus.RUNNING, loaded.get().status());
        assertEquals(2L, loaded.get().version());

        tech.kayys.andalus.harness.consistency.state.ExecutionId jId =
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId.value());
        assertTrue(journal.history(jId).stream().anyMatch(e -> e.type() == ExecutionEventType.EXECUTION_RESUMING));
        assertTrue(journal.history(jId).stream().anyMatch(e -> e.type() == ExecutionEventType.EXECUTION_STARTED));
    }

    @Test
    void testResumeRejectsNonSuspendedExecution() {
        assertThrows(IllegalStateException.class, () -> control.resume(executionId));
    }

    @Test
    void testCancellationTransitionsToCanceledAndSignalsRegistry() {
        AtomicBoolean signaled = new AtomicBoolean(false);
        cancellationRegistry.register(executionId, () -> signaled.set(true));

        CancellationHandle handle = control.cancel(executionId, CancellationMode.IMMEDIATE, "Stop requested");
        assertTrue(handle.isCanceled());
        assertEquals(CancellationMode.IMMEDIATE, handle.mode());

        Optional<ExecutionState> loaded = stateStore.load(executionId);
        assertTrue(loaded.isPresent());
        assertEquals(ExecutionStatus.CANCELED, loaded.get().status());

        assertTrue(signaled.get());

        tech.kayys.andalus.harness.consistency.state.ExecutionId jId =
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId.value());
        assertTrue(journal.history(jId).stream().anyMatch(e -> e.type() == ExecutionEventType.EXECUTION_CANCELLED));
    }

    @Test
    void testCancellationIsIdempotentOnTerminalExecution() {
        control.cancel(executionId, CancellationMode.IMMEDIATE, "First cancel");

        CancellationHandle secondHandle = control.cancel(executionId, CancellationMode.IMMEDIATE, "Second cancel");
        assertFalse(secondHandle.isCanceled()); // indicates already terminal
    }

    @Test
    void testCheckTimeoutEnforcesDeadline() {
        Instant now = Instant.now();
        Deadline deadline = new Deadline(now.minusSeconds(1));

        boolean timedOut = control.checkTimeout(executionId, deadline, now);
        assertTrue(timedOut);

        Optional<ExecutionState> loaded = stateStore.load(executionId);
        assertTrue(loaded.isPresent());
        assertEquals(ExecutionStatus.TIMEOUT, loaded.get().status());

        tech.kayys.andalus.harness.consistency.state.ExecutionId jId =
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId.value());
        assertTrue(journal.history(jId).stream().anyMatch(e -> e.type() == ExecutionEventType.EXECUTION_EXPIRED));
    }

    @Test
    void testCheckTimeoutReturnsFalseBeforeDeadline() {
        Instant now = Instant.now();
        Deadline deadline = new Deadline(now.plus(Duration.ofMinutes(10)));

        boolean timedOut = control.checkTimeout(executionId, deadline, now);
        assertFalse(timedOut);

        Optional<ExecutionState> loaded = stateStore.load(executionId);
        assertTrue(loaded.isPresent());
        assertEquals(ExecutionStatus.RUNNING, loaded.get().status());
    }

    @Test
    void testHarnessExecutionDelegation() {
        DefaultHarnessLifecycle lifecycle = new DefaultHarnessLifecycle(HarnessExecutionStatus.RUNNING);

        DefaultHarnessExecution harnessExecution = new DefaultHarnessExecution(
                executionId.value(),
                lifecycle,
                control
        );

        // Suspend delegates durably
        harnessExecution.suspend();
        assertEquals(HarnessExecutionStatus.SUSPENDED, harnessExecution.status());
        assertEquals(ExecutionStatus.SUSPENDED, stateStore.load(executionId).get().status());

        // Resume delegates durably (transitions to RESUMING in lifecycle, RUNNING in durable state)
        harnessExecution.resume();
        assertEquals(HarnessExecutionStatus.RESUMING, harnessExecution.status());
        assertEquals(ExecutionStatus.RUNNING, stateStore.load(executionId).get().status());

        // Complete transition to RUNNING in lifecycle
        lifecycle.transitionTo(HarnessExecutionStatus.RUNNING);
        assertEquals(HarnessExecutionStatus.RUNNING, harnessExecution.status());

        // Cancel delegates durably
        harnessExecution.cancel();
        assertEquals(HarnessExecutionStatus.CANCELLED, harnessExecution.status());
        assertEquals(ExecutionStatus.CANCELED, stateStore.load(executionId).get().status());
        assertTrue(harnessExecution.completion().isDone());
    }
}
