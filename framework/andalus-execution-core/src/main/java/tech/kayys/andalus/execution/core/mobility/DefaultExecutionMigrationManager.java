package tech.kayys.andalus.execution.core.mobility;

import tech.kayys.andalus.execution.mobility.ExecutionMigrationManager;
import tech.kayys.andalus.execution.mobility.MigrationCoordinator;
import tech.kayys.andalus.execution.mobility.MigrationPreparation;
import tech.kayys.andalus.execution.mobility.MigrationReason;
import tech.kayys.andalus.execution.mobility.MigrationResult;

import java.util.Objects;

/**
 * Default implementation of {@link ExecutionMigrationManager}.
 */
public class DefaultExecutionMigrationManager implements ExecutionMigrationManager {

    private final MigrationCoordinator coordinator;

    public DefaultExecutionMigrationManager(MigrationCoordinator coordinator) {
        this.coordinator = Objects.requireNonNull(coordinator, "coordinator cannot be null");
    }

    @Override
    public MigrationResult migrate(String executionId, String targetRuntimeId, MigrationReason reason) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(targetRuntimeId, "targetRuntimeId cannot be null");

        MigrationPreparation prep = coordinator.prepare(executionId, targetRuntimeId, reason);
        return coordinator.commit(prep);
    }
}
