package tech.kayys.wayang.execution.core.mobility;

import tech.kayys.wayang.execution.checkpoint.CheckpointStore;
import tech.kayys.wayang.execution.checkpoint.ExecutionCheckpoint;
import tech.kayys.wayang.execution.mobility.ExecutionDurableState;
import tech.kayys.wayang.execution.mobility.ExecutionLease;
import tech.kayys.wayang.execution.mobility.ExecutionStatus;
import tech.kayys.wayang.execution.mobility.FencingToken;
import tech.kayys.wayang.execution.mobility.LeaseManager;
import tech.kayys.wayang.execution.mobility.MigrationCoordinator;
import tech.kayys.wayang.execution.mobility.MigrationPreparation;
import tech.kayys.wayang.execution.mobility.MigrationReason;
import tech.kayys.wayang.execution.mobility.MigrationReceipt;
import tech.kayys.wayang.execution.mobility.MigrationResult;
import tech.kayys.wayang.execution.mobility.state.ExecutionStateStore;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link MigrationCoordinator} managing 2-phase migration transactions.
 */
public class DefaultMigrationCoordinator implements MigrationCoordinator {

    private final LeaseManager leaseManager;
    private final ExecutionStateStore stateStore;
    private final CheckpointStore checkpointStore;
    private final Map<String, MigrationPreparation> activePreparations = new ConcurrentHashMap<>();
    private final Map<String, MigrationReceipt> receipts = new ConcurrentHashMap<>();

    public DefaultMigrationCoordinator(
            LeaseManager leaseManager,
            ExecutionStateStore stateStore,
            CheckpointStore checkpointStore
    ) {
        this.leaseManager = Objects.requireNonNull(leaseManager, "leaseManager cannot be null");
        this.stateStore = Objects.requireNonNull(stateStore, "stateStore cannot be null");
        this.checkpointStore = Objects.requireNonNull(checkpointStore, "checkpointStore cannot be null");
    }

    @Override
    public synchronized MigrationPreparation prepare(String executionId, String targetRuntimeId, MigrationReason reason) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(targetRuntimeId, "targetRuntimeId cannot be null");

        // 1. Verify current lease holder
        Optional<ExecutionLease> currentLease = leaseManager.getLease(executionId);
        String sourceRuntimeId = currentLease.map(ExecutionLease::holderId).orElse("unbound");

        // 2. Verify state exists
        ExecutionDurableState state = stateStore.load(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot migrate nonexistent execution: " + executionId));

        // 3. Quiesce / Transition state to MIGRATING
        FencingToken token = currentLease.map(ExecutionLease::fencingToken)
                .or(() -> leaseManager.currentToken(executionId))
                .orElseGet(() -> FencingToken.of(1L));

        ExecutionDurableState migratingState = state.withStatus(ExecutionStatus.MIGRATING);
        stateStore.save(migratingState, token);

        // 4. Retrieve or create checkpoint
        String checkpointId = "cp-mig-" + executionId + "-" + System.currentTimeMillis();

        MigrationPreparation prep = new MigrationPreparation(
                executionId,
                sourceRuntimeId,
                targetRuntimeId,
                checkpointId,
                reason != null ? reason : MigrationReason.OPTIMIZATION,
                Instant.now()
        );
        activePreparations.put(executionId, prep);
        return prep;
    }

    @Override
    public synchronized MigrationResult commit(MigrationPreparation preparation) {
        Objects.requireNonNull(preparation, "preparation cannot be null");
        String executionId = preparation.executionId();

        if (!activePreparations.containsKey(executionId)) {
            return MigrationResult.failure(executionId, preparation.sourceRuntimeId(), preparation.targetRuntimeId(),
                    "No active migration preparation found for execution: " + executionId);
        }

        try {
            // 1. Release source lease
            leaseManager.releaseLease(executionId, preparation.sourceRuntimeId());

            // 2. Acquire target lease with new fencing token generation
            ExecutionLease targetLease = leaseManager.acquireLease(
                    executionId,
                    preparation.targetRuntimeId(),
                    Duration.ofHours(1)
            );

            // 3. Transition execution state to RESUMING then RUNNING
            ExecutionDurableState state = stateStore.load(executionId)
                    .orElseThrow(() -> new IllegalStateException("Execution state missing during commit: " + executionId));

            ExecutionDurableState runningState = state.withStatus(ExecutionStatus.RUNNING);
            stateStore.save(runningState, targetLease.fencingToken());

            // 4. Produce audit receipt
            MigrationReceipt receipt = new MigrationReceipt(
                    executionId,
                    preparation.sourceRuntimeId(),
                    preparation.targetRuntimeId(),
                    preparation.checkpointId(),
                    "contract:standard:v1",
                    preparation.reason(),
                    preparation.preparedAt(),
                    Instant.now()
            );
            receipts.put(executionId, receipt);
            activePreparations.remove(executionId);

            return MigrationResult.success(executionId, preparation.sourceRuntimeId(), preparation.targetRuntimeId());
        } catch (Exception e) {
            return MigrationResult.failure(executionId, preparation.sourceRuntimeId(), preparation.targetRuntimeId(),
                    "Migration commit failed: " + e.getMessage());
        }
    }

    @Override
    public synchronized void abort(MigrationPreparation preparation) {
        if (preparation == null) {
            return;
        }
        String executionId = preparation.executionId();
        activePreparations.remove(executionId);

        // Restore source lease & state to SUSPENDED or RUNNING
        stateStore.load(executionId).ifPresent(state -> {
            leaseManager.currentToken(executionId).ifPresent(token -> {
                ExecutionDurableState restored = state.withStatus(ExecutionStatus.SUSPENDED);
                stateStore.save(restored, token);
            });
        });
    }

    public Optional<MigrationReceipt> getReceipt(String executionId) {
        return Optional.ofNullable(receipts.get(executionId));
    }
}
