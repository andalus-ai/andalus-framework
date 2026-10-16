package tech.kayys.andalus.a2a.durable;

/**
 * Narrow SPI that translates an A2A task terminal event into a wake of the local
 * Claw execution (6.16.4.9).
 *
 * <p>The implementation is supplied by the runtime and typically delegates to the
 * harness {@code DurableWaitController}, which reloads durable state, validates the
 * execution is still WAITING (not terminal, not expired), and performs the
 * WAITING → RESUMING transition. Stale or duplicate wakes are harmless because the
 * wake always re-validates durable state.</p>
 *
 * <p>This SPI does not own execution state, checkpoints, journal, retry, recovery,
 * or scheduling.</p>
 */
@FunctionalInterface
public interface A2AExecutionWakeup {

    /**
     * Requests a wake of the given local caller execution.
     *
     * @param callerExecutionId the local Claw execution that initiated the A2A call
     */
    void wake(String callerExecutionId);
}
