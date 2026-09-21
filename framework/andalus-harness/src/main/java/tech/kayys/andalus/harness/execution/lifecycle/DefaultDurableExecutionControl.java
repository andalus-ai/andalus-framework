package tech.kayys.andalus.harness.execution.lifecycle;

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
import tech.kayys.andalus.harness.scheduling.cancellation.CancellationHandle;
import tech.kayys.andalus.harness.scheduling.cancellation.CancellationMode;
import tech.kayys.andalus.harness.scheduling.cancellation.CancellationRegistry;
import tech.kayys.andalus.harness.scheduling.cancellation.DefaultCancellationHandle;
import tech.kayys.andalus.harness.scheduling.temporal.Deadline;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Default implementation of {@link DurableExecutionControl}.
 *
 * <p>Coordinates state persistence, checkpoint creation, journal event appending,
 * and cancellation signaling across durable execution lifecycle transitions.</p>
 */
public class DefaultDurableExecutionControl implements DurableExecutionControl {

    private final ExecutionStateStore stateStore;
    private final CheckpointManager checkpointManager;
    private final ExecutionJournal journal;
    private final CancellationRegistry cancellationRegistry;

    public DefaultDurableExecutionControl(
            ExecutionStateStore stateStore,
            CheckpointManager checkpointManager
    ) {
        this(stateStore, checkpointManager, null, null);
    }

    public DefaultDurableExecutionControl(
            ExecutionStateStore stateStore,
            CheckpointManager checkpointManager,
            ExecutionJournal journal,
            CancellationRegistry cancellationRegistry
    ) {
        this.stateStore = Objects.requireNonNull(stateStore, "stateStore");
        this.checkpointManager = Objects.requireNonNull(checkpointManager, "checkpointManager");
        this.journal = journal;
        this.cancellationRegistry = cancellationRegistry;
    }

    @Override
    public void suspend(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId");
        ExecutionState current = loadRequired(executionId);

        if (current.status().isTerminal()) {
            throw new IllegalStateException("Cannot suspend terminal execution " + executionId.value() +
                    " in status " + current.status());
        }

        if (current.status() == ExecutionStatus.SUSPENDED) {
            return;
        }

        if (current.status() != ExecutionStatus.RUNNING && current.status() != ExecutionStatus.WAITING) {
            throw new IllegalStateException("Cannot suspend execution " + executionId.value() +
                    " from status " + current.status());
        }

        // 1. Capture checkpoint boundary
        checkpointManager.create(current);

        // 2. Append journal event
        recordEvent(executionId, ExecutionEventType.EXECUTION_SUSPENDED, Map.of());

        // 3. Persist SUSPENDED state
        ExecutionState suspendedState = transitionStatus(current, ExecutionStatus.SUSPENDED);
        stateStore.save(suspendedState, current.version());
    }

    @Override
    public void resume(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId");
        ExecutionState current = loadRequired(executionId);

        if (current.status().isTerminal()) {
            throw new IllegalStateException("Cannot resume terminal execution " + executionId.value() +
                    " in status " + current.status());
        }

        if (current.status() != ExecutionStatus.SUSPENDED) {
            throw new IllegalStateException("Cannot resume execution " + executionId.value() +
                    ": expected SUSPENDED but was " + current.status());
        }

        // 1. Append resuming intent
        recordEvent(executionId, ExecutionEventType.EXECUTION_RESUMING, Map.of());

        // 2. Restore latest checkpoint boundary
        checkpointManager.latest(executionId);

        // 3. Persist RUNNING state
        ExecutionState runningState = transitionStatus(current, ExecutionStatus.RUNNING);
        stateStore.save(runningState, current.version());

        // 4. Append started event
        recordEvent(executionId, ExecutionEventType.EXECUTION_STARTED, Map.of());
    }

    @Override
    public CancellationHandle cancel(ExecutionId executionId, CancellationMode mode, String reason) {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(mode, "mode");
        ExecutionState current = loadRequired(executionId);

        if (current.status().isTerminal()) {
            return DefaultCancellationHandle.alreadyTerminal(executionId, mode);
        }

        // 1. Record journal cancellation event
        recordEvent(executionId, ExecutionEventType.EXECUTION_CANCELLED, Map.of(
                "mode", mode.name(),
                "reason", reason != null ? reason : ""
        ));

        // 2. Persist CANCELED state
        ExecutionState canceledState = transitionStatus(current, ExecutionStatus.CANCELED);
        stateStore.save(canceledState, current.version());

        // 3. Signal local cancellation token
        if (cancellationRegistry != null) {
            cancellationRegistry.signal(executionId);
        }

        return DefaultCancellationHandle.accepted(executionId, mode);
    }

    @Override
    public boolean checkTimeout(ExecutionId executionId, Deadline deadline, Instant now) {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(deadline, "deadline");
        Objects.requireNonNull(now, "now");

        if (!deadline.isExceeded(now)) {
            return false;
        }

        ExecutionState current = loadRequired(executionId);
        if (current.status().isTerminal()) {
            return true;
        }

        // 1. Record expired journal event
        recordEvent(executionId, ExecutionEventType.EXECUTION_EXPIRED, Map.of(
                "deadline", deadline.at().toString(),
                "expiredAt", now.toString()
        ));

        // 2. Persist TIMEOUT state
        ExecutionState timedOutState = transitionStatus(current, ExecutionStatus.TIMEOUT);
        stateStore.save(timedOutState, current.version());

        // 3. Signal local cancellation token
        if (cancellationRegistry != null) {
            cancellationRegistry.signal(executionId);
        }

        return true;
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
                    java.util.Optional.empty(),
                    payload
            ));
        }
    }
}
