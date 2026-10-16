package tech.kayys.andalus.harness.execution.lifecycle;

import tech.kayys.andalus.harness.execution.state.ExecutionStatus;
import tech.kayys.andalus.harness.lifecycle.HarnessExecutionStatus;

/**
 * Bidirectional mapping between {@link HarnessExecutionStatus} and {@link ExecutionStatus} (6.15.3.3).
 *
 * <p>Two status vocabularies are intentionally kept separate:
 * <ul>
 *   <li>{@code HarnessExecutionStatus} — fine-grained in-process lifecycle states
 *       (INITIALIZING, ADMITTED, RESUMING, EXPIRED, …)</li>
 *   <li>{@code ExecutionStatus} — coarse durable states persisted to the state store</li>
 * </ul>
 *
 * <p>Many harness states map to the same durable state because the durable record
 * does not need to distinguish admission/initialisation phases — only the
 * execution-significant boundaries matter for crash recovery and reconciliation.
 */
final class ExecutionStatusMapper {

    private ExecutionStatusMapper() {}

    /**
     * Maps a harness lifecycle status to its canonical durable counterpart.
     *
     * @param status the in-process harness status
     * @return the durable status to persist
     * @throws IllegalStateException if no mapping is defined
     */
    static ExecutionStatus durable(HarnessExecutionStatus status) {
        return switch (status) {
            case CREATED                        -> ExecutionStatus.CREATED;
            case ADMITTED, INITIALIZING, RUNNING -> ExecutionStatus.RUNNING;
            case WAITING                         -> ExecutionStatus.WAITING;
            case SUSPENDED                       -> ExecutionStatus.SUSPENDED;
            case RESUMING                        -> ExecutionStatus.RUNNING; // in-flight restoration
            case COMPLETED                       -> ExecutionStatus.COMPLETED;
            case FAILED                          -> ExecutionStatus.FAILED;
            case CANCELLED                       -> ExecutionStatus.CANCELED;
            case EXPIRED                         -> ExecutionStatus.TIMEOUT;
        };
    }

    /**
     * Maps a durable execution status back to a harness lifecycle status.
     *
     * <p>Used when reconstructing in-process lifecycle from a recovered durable state.
     *
     * @param status the persisted durable status
     * @return the harness status to restore
     * @throws IllegalStateException if no mapping is defined
     */
    static HarnessExecutionStatus harness(ExecutionStatus status) {
        return switch (status) {
            case CREATED    -> HarnessExecutionStatus.CREATED;
            case RUNNING    -> HarnessExecutionStatus.RUNNING;
            case WAITING    -> HarnessExecutionStatus.WAITING;
            case SUSPENDED  -> HarnessExecutionStatus.SUSPENDED;
            case RECOVERING -> HarnessExecutionStatus.RESUMING;
            case COMPLETED  -> HarnessExecutionStatus.COMPLETED;
            case FAILED     -> HarnessExecutionStatus.FAILED;
            case CANCELED   -> HarnessExecutionStatus.CANCELLED;
            case TIMEOUT    -> HarnessExecutionStatus.EXPIRED;
        };
    }
}
