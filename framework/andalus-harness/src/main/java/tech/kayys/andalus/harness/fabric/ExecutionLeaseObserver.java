package tech.kayys.andalus.harness.fabric;

/**
 * Receives best-effort observations about execution lease lifecycle and renewal.
 *
 * <p>Observers must be fast and non-blocking. Runtime exceptions thrown by an
 * observer are isolated and never change lease ownership or task completion.</p>
 */
@FunctionalInterface
public interface ExecutionLeaseObserver {
    ExecutionLeaseObserver NOOP = observation -> { };

    void onObservation(ExecutionLeaseObservation observation);
}
