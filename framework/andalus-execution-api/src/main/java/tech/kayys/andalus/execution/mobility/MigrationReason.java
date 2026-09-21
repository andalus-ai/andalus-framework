package tech.kayys.andalus.execution.mobility;

/**
 * Reasons why an execution is being migrated.
 */
public enum MigrationReason {
    RUNTIME_FAILURE,
    RESOURCE_PRESSURE,
    POLICY_CHANGE,
    SCALE,
    MAINTENANCE,
    LOCAL_TO_REMOTE,
    REMOTE_TO_LOCAL,
    USER_REQUEST,
    OPTIMIZATION
}
