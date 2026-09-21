package tech.kayys.andalus.harness.execution;

import tech.kayys.andalus.harness.execution.action.ActionJournal;
import tech.kayys.andalus.harness.execution.action.InMemoryActionJournal;
import tech.kayys.andalus.harness.execution.checkpoint.Checkpoint;
import tech.kayys.andalus.harness.execution.checkpoint.CheckpointId;
import tech.kayys.andalus.harness.execution.checkpoint.CheckpointManager;
import tech.kayys.andalus.harness.execution.checkpoint.InMemoryCheckpointManager;
import tech.kayys.andalus.harness.execution.recovery.DefaultRecoveryStrategy;
import tech.kayys.andalus.harness.execution.recovery.RecoveryContext;
import tech.kayys.andalus.harness.execution.recovery.RecoveryDecision;
import tech.kayys.andalus.harness.execution.recovery.RecoveryStrategy;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionStateStore;
import tech.kayys.andalus.harness.execution.state.InMemoryExecutionStateStore;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Provides the default implementation of the execution coordinator contract.
 */


public class DefaultExecutionCoordinator implements ExecutionCoordinator {

    private final ExecutionStateStore stateStore;
    private final CheckpointManager checkpoints;
    private final ActionJournal journal;
    private final RecoveryStrategy recovery;

    public DefaultExecutionCoordinator() {
        this(new InMemoryExecutionStateStore(), new InMemoryCheckpointManager(), new InMemoryActionJournal(), new DefaultRecoveryStrategy());
    }

    public DefaultExecutionCoordinator(
            ExecutionStateStore stateStore,
            CheckpointManager checkpoints,
            ActionJournal journal,
            RecoveryStrategy recovery) {
        this.stateStore = Objects.requireNonNull(stateStore, "stateStore");
        this.checkpoints = Objects.requireNonNull(checkpoints, "checkpoints");
        this.journal = Objects.requireNonNull(journal, "journal");
        this.recovery = Objects.requireNonNull(recovery, "recovery");
    }

    @Override public ExecutionStateStore stateStore() { return stateStore; }
    @Override public CheckpointManager checkpoints() { return checkpoints; }
    @Override public ActionJournal journal() { return journal; }
    @Override public RecoveryStrategy recovery() { return recovery; }

    @Override
    public CheckpointId checkpoint(ExecutionState state) {
        return checkpoint(state, tech.kayys.andalus.harness.journal.JournalPosition.beginning());
    }

    @Override
    public CheckpointId checkpoint(ExecutionState state, tech.kayys.andalus.harness.journal.JournalPosition position) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(position, "position");
        Optional<ExecutionState> current = stateStore.load(state.executionId());
        long expected = current.map(ExecutionState::version).orElse(state.version());
        stateStore.save(state, expected);
        return checkpoints.create(state, position);
    }

    @Override
    public Optional<ExecutionState> restore(ExecutionId id) {
        Objects.requireNonNull(id, "id");
        Optional<Checkpoint> latestCp = checkpoints.latest(id);
        if (latestCp.isPresent()) {
            return Optional.of(latestCp.get().state());
        }
        return stateStore.load(id);
    }

    @Override
    public RecoveryDecision recover(ExecutionId id, Throwable failure) {
        Objects.requireNonNull(id, "id");
        Optional<ExecutionState> lastState = restore(id);
        // Provide action history so the strategy can inspect STARTED actions (6.13)
        java.util.List<tech.kayys.andalus.harness.execution.action.ActionRecord> actions = journal.history(id);
        RecoveryContext ctx = new RecoveryContext(id, lastState, Optional.ofNullable(failure),
                Map.of("actionHistory", actions));
        return recovery.recover(lastState.orElse(null), ctx);
    }
}
