package tech.kayys.andalus.harness.execution;

import tech.kayys.andalus.harness.execution.action.ActionJournal;
import tech.kayys.andalus.harness.execution.checkpoint.CheckpointId;
import tech.kayys.andalus.harness.execution.checkpoint.CheckpointManager;
import tech.kayys.andalus.harness.execution.recovery.RecoveryDecision;
import tech.kayys.andalus.harness.execution.recovery.RecoveryStrategy;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionStateStore;

import java.util.Optional;

/**
 * High-level coordinator unifying execution state, checkpointing, action journaling, and recovery.
 */
public interface ExecutionCoordinator {

    ExecutionStateStore stateStore();

    CheckpointManager checkpoints();

    ActionJournal journal();

    RecoveryStrategy recovery();

    CheckpointId checkpoint(ExecutionState state);

    default CheckpointId checkpoint(ExecutionState state, tech.kayys.andalus.harness.journal.JournalPosition position) {
        return checkpoint(state);
    }

    Optional<ExecutionState> restore(ExecutionId id);

    RecoveryDecision recover(ExecutionId id, Throwable failure);
}
