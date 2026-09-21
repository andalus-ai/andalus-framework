package tech.kayys.andalus.harness.scheduling.temporal;

/**
 * Outcome of a {@link DurableWaitCoordinator#wakeExecution} call.
 */
public enum WakeResult {

    /** Execution was successfully transitioned from WAITING to RUNNING. */
    WOKEN,

    /**
     * Wake was ignored because the execution was already terminal (COMPLETED, FAILED, etc.)
     * or was no longer in WAITING state. This is the idempotent no-op case.
     */
    IGNORED,

    /** Execution's deadline was exceeded at wake time — transitioned to TIMEOUT instead of RUNNING. */
    EXPIRED,

    /**
     * Execution was not found in durable storage. Caller should treat as a stale/ghost wake event.
     */
    NOT_FOUND
}
