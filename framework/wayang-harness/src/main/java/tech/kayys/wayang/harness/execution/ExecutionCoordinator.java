package tech.kayys.wayang.harness.execution;

import tech.kayys.wayang.harness.execution.action.ActionJournal;
import tech.kayys.wayang.harness.execution.checkpoint.CheckpointId;
import tech.kayys.wayang.harness.execution.checkpoint.CheckpointManager;
import tech.kayys.wayang.harness.execution.recovery.RecoveryDecision;
import tech.kayys.wayang.harness.execution.recovery.RecoveryStrategy;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.execution.state.ExecutionState;
import tech.kayys.wayang.harness.execution.state.ExecutionStateStore;

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

    Optional<ExecutionState> restore(ExecutionId id);

    RecoveryDecision recover(ExecutionId id, Throwable failure);
}
