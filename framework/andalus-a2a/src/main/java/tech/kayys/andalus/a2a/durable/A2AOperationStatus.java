package tech.kayys.andalus.a2a.durable;

/**
 * Status of a local A2A operation from the Claw execution perspective (6.16).
 *
 * <p>This is deliberately NOT the same as {@code A2ATaskStatus} (the remote protocol
 * status). Local Claw execution is authoritative; remote task status is a projection (6.16.22).
 */
public enum A2AOperationStatus {

    /** Operation created locally but remote request not yet sent. */
    PENDING,

    /** Request submitted to remote agent; awaiting acknowledgement. */
    SUBMITTED,

    /** Remote agent accepted — local execution is now WAITING for remote completion (6.16.7). */
    WAITING_FOR_REMOTE,

    /** Remote execution completed — result persisted, wake triggered (6.16.21). */
    COMPLETED,

    /** Remote execution failed or timed out at A2A level. */
    FAILED,

    /**
     * Outcome unknown after crash (6.16.14).
     * Requires reconciliation before proceeding: may find SUBMITTED, WAITING, or COMPLETED.
     */
    UNKNOWN,

    /** Local execution or remote A2A task cancelled (6.16.16). */
    CANCELLED;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    public boolean requiresReconciliation() {
        return this == UNKNOWN || this == SUBMITTED;
    }
}
