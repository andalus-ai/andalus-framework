package tech.kayys.wayang.harness.execution;

import tech.kayys.wayang.harness.execution.action.ActionJournal;
import tech.kayys.wayang.harness.execution.action.InMemoryActionJournal;
import tech.kayys.wayang.harness.execution.checkpoint.Checkpoint;
import tech.kayys.wayang.harness.execution.checkpoint.CheckpointId;
import tech.kayys.wayang.harness.execution.checkpoint.CheckpointManager;
import tech.kayys.wayang.harness.execution.checkpoint.InMemoryCheckpointManager;
import tech.kayys.wayang.harness.execution.recovery.DefaultRecoveryStrategy;
import tech.kayys.wayang.harness.execution.recovery.RecoveryContext;
import tech.kayys.wayang.harness.execution.recovery.RecoveryDecision;
import tech.kayys.wayang.harness.execution.recovery.RecoveryStrategy;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.execution.state.ExecutionState;
import tech.kayys.wayang.harness.execution.state.ExecutionStateStore;
import tech.kayys.wayang.harness.execution.state.InMemoryExecutionStateStore;

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
        Objects.requireNonNull(state, "state");
        Optional<ExecutionState> current = stateStore.load(state.executionId());
        long expected = current.map(ExecutionState::version).orElse(state.version());
        stateStore.save(state, expected);
        return checkpoints.create(state);
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
        RecoveryContext ctx = new RecoveryContext(id, lastState, Optional.ofNullable(failure), Map.of());
        return recovery.recover(lastState.orElse(null), ctx);
    }
}
