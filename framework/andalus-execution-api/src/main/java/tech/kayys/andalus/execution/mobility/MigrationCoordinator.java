package tech.kayys.andalus.execution.mobility;

/**
 * Two-phase coordinator for atomic execution migration across runtimes.
 */
public interface MigrationCoordinator {

    /**
     * Phase 1: Prepare migration to target runtime by validating admission, taking durable checkpoint.
     */
    MigrationPreparation prepare(String executionId, String targetRuntimeId, MigrationReason reason);

    /**
     * Phase 2: Commit migration by fencing source, activating target lease, restoring state.
     */
    MigrationResult commit(MigrationPreparation preparation);

    /**
     * Abort a prepared migration, rolling back and preserving source execution lease.
     */
    void abort(MigrationPreparation preparation);
}
