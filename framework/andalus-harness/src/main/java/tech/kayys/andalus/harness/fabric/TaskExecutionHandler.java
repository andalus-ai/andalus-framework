package tech.kayys.andalus.harness.fabric;

/**
 * Executes one claimed assignment.
 *
 * <p>The implementation must make its durable completion transition before
 * returning normally. It should use the supplied claim token when writing
 * fenced execution state. Queue acknowledgement happens only after this
 * method returns successfully.</p>
 */
@FunctionalInterface
public interface TaskExecutionHandler {
    void execute(TaskClaim claim, ClaimVisibility visibility) throws Exception;
}
