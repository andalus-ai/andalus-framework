package tech.kayys.wayang.execution.mobility;

/**
 * High-level manager coordinating end-to-end execution migration.
 */
public interface ExecutionMigrationManager {

    /**
     * Migrate the given execution to target runtime with the specified reason.
     */
    MigrationResult migrate(String executionId, String targetRuntimeId, MigrationReason reason);
}
