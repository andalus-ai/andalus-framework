package tech.kayys.wayang.execution.core.mobility;

import tech.kayys.wayang.execution.attempt.AttemptId;
import tech.kayys.wayang.execution.checkpoint.CheckpointIntegrity;
import tech.kayys.wayang.execution.checkpoint.CheckpointManifest;
import tech.kayys.wayang.execution.checkpoint.CheckpointStore;
import tech.kayys.wayang.execution.checkpoint.ExecutionCheckpoint;
import tech.kayys.wayang.execution.mobility.ExecutionDurableState;
import tech.kayys.wayang.execution.mobility.ExecutionMigrationManager;
import tech.kayys.wayang.execution.mobility.ExecutionMobility;
import tech.kayys.wayang.execution.mobility.ExecutionStatus;
import tech.kayys.wayang.execution.mobility.FencingToken;
import tech.kayys.wayang.execution.mobility.LeaseManager;
import tech.kayys.wayang.execution.mobility.MigrationReason;
import tech.kayys.wayang.execution.mobility.MigrationResult;
import tech.kayys.wayang.execution.mobility.state.ExecutionStateStore;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default unified implementation of {@link ExecutionMobility}.
 */
public class DefaultExecutionMobility implements ExecutionMobility {

    private final ExecutionStateStore stateStore;
    private final CheckpointStore checkpointStore;
    private final LeaseManager leaseManager;
    private final ExecutionMigrationManager migrationManager;

    public DefaultExecutionMobility(
            ExecutionStateStore stateStore,
            CheckpointStore checkpointStore,
            LeaseManager leaseManager,
            ExecutionMigrationManager migrationManager
    ) {
        this.stateStore = Objects.requireNonNull(stateStore, "stateStore cannot be null");
        this.checkpointStore = Objects.requireNonNull(checkpointStore, "checkpointStore cannot be null");
        this.leaseManager = Objects.requireNonNull(leaseManager, "leaseManager cannot be null");
        this.migrationManager = Objects.requireNonNull(migrationManager, "migrationManager cannot be null");
    }

    @Override
    public ExecutionCheckpoint checkpoint(String executionId) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        ExecutionDurableState state = stateStore.load(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));

        FencingToken token = leaseManager.currentToken(executionId).orElseGet(() -> FencingToken.of(1L));
        ExecutionDurableState checkpointingState = state.withStatus(ExecutionStatus.CHECKPOINTING);
        stateStore.save(checkpointingState, token);

        String cpId = "cp-" + executionId + "-" + System.currentTimeMillis();
        byte[] payload = ("state-seq:" + state.versionSequence()).getBytes();
        CheckpointManifest manifest = new CheckpointManifest(
                cpId,
                state.versionSequence(),
                List.of(),
                List.of(),
                List.of(),
                CheckpointIntegrity.sha256(payload),
                Instant.now(),
                Map.of()
        );

        ExecutionCheckpoint cp = new ExecutionCheckpoint(
                cpId,
                executionId,
                AttemptId.of(String.valueOf(state.cursor().attempt())),
                state.versionSequence(),
                payload,
                manifest,
                Instant.now(),
                Map.of("status", ExecutionStatus.CHECKPOINTING.name())
        );
        checkpointStore.save(cp);

        ExecutionDurableState runningState = checkpointingState.withStatus(ExecutionStatus.RUNNING);
        stateStore.save(runningState, token);
        return cp;
    }

    @Override
    public void suspend(String executionId) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        ExecutionDurableState state = stateStore.load(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));

        FencingToken token = leaseManager.currentToken(executionId).orElseGet(() -> FencingToken.of(1L));
        ExecutionDurableState suspendedState = state.withStatus(ExecutionStatus.SUSPENDED);
        stateStore.save(suspendedState, token);

        leaseManager.getLease(executionId).ifPresent(l -> leaseManager.releaseLease(executionId, l.holderId()));
    }

    @Override
    public void restore(String executionId, String checkpointId) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(checkpointId, "checkpointId cannot be null");

        checkpointStore.get(checkpointId)
                .orElseThrow(() -> new IllegalArgumentException("Checkpoint not found: " + checkpointId));

        ExecutionDurableState state = stateStore.load(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));

        FencingToken token = leaseManager.currentToken(executionId).orElseGet(() -> FencingToken.of(1L));
        ExecutionDurableState resumedState = state.withStatus(ExecutionStatus.RUNNING);
        stateStore.save(resumedState, token);
    }

    @Override
    public MigrationResult migrate(String executionId, String targetRuntimeId, MigrationReason reason) {
        return migrationManager.migrate(executionId, targetRuntimeId, reason);
    }
}
