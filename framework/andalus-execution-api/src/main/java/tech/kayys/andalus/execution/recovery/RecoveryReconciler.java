package tech.kayys.andalus.execution.recovery;

/**
 * Repairs interrupted recovery transitions after their owning lease is no longer active.
 * Implementations must acquire a fresh fencing generation before writing any state.
 */
@FunctionalInterface
public interface RecoveryReconciler {
    RecoveryReconciliationResult reconcile(String executionId);
}
