package tech.kayys.andalus.execution.recovery;

/**
 * Port for durable delayed recovery dispatch.
 *
 * Implementations must persist the schedule before acknowledging it and enforce uniqueness
 * by idempotencyKey. Delivery is at-least-once; consumers must still claim a fenced execution
 * lease before changing execution state or starting recovery.
 */
public interface RecoveryScheduler {
    /** Enqueue or return the existing schedule for the same idempotency key. */
    void schedule(RecoverySchedule schedule);

    /** Cancel a pending schedule; false means no pending schedule was found. */
    boolean cancel(String idempotencyKey);
}
