package tech.kayys.wayang.execution.mobility;

import tech.kayys.wayang.execution.checkpoint.ExecutionCheckpoint;

/**
 * Unified SPI for execution mobility: checkpoint, suspend, restore, and migrate.
 */
public interface ExecutionMobility {

    /**
     * Create a durable checkpoint at current execution boundary.
     */
    ExecutionCheckpoint checkpoint(String executionId);

    /**
     * Suspend execution and release active runtime execution lease.
     */
    void suspend(String executionId);

    /**
     * Restore execution from an existing durable checkpoint.
     */
    void restore(String executionId, String checkpointId);

    /**
     * Atomically migrate execution to target runtime.
     */
    MigrationResult migrate(String executionId, String targetRuntimeId, MigrationReason reason);
}
