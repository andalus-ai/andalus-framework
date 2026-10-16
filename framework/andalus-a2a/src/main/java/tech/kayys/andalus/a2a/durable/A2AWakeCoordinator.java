package tech.kayys.andalus.a2a.durable;

/**
 * A2A integration SPI bridging the A2A task lifecycle to local execution wakes
 * (6.16.4.9). This is deliberately <em>not</em> a new general waiting abstraction —
 * the durable waiting machinery remains the harness {@code DurableWaitController}
 * and {@code WakeCondition} model.
 *
 * <p>Registration is transient by design; the durable source of truth remains the
 * {@link DurableA2ATaskLedger}, from which registrations can be reconstructed
 * during startup recovery (6.16.4.10).</p>
 */
public interface A2AWakeCoordinator {

    /**
     * Associates an A2A task with the local caller execution that must be woken
     * when the task resolves.
     *
     * @param callerExecutionId local Claw execution id
     * @param taskId            A2A protocol task id
     * @throws IllegalArgumentException if the task is unknown or belongs to a
     *                                  different caller execution
     */
    void register(String callerExecutionId, String taskId);

    /** Wakes the caller execution of the given task (completion path). */
    void complete(String taskId);

    /** Wakes the caller execution of the given task (failure path). */
    void fail(String taskId);

    /** Wakes the caller execution of the given task (cancellation path). */
    void cancel(String taskId);
}
