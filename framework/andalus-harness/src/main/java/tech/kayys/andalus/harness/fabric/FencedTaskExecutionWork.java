package tech.kayys.andalus.harness.fabric;

import tech.kayys.andalus.execution.mobility.FencingToken;

/**
 * Performs task work while holding the execution lease's fencing generation.
 *
 * <p>Implementations should propagate the token to any downstream state writer
 * that supports fencing. Queue claim generations must never be substituted for
 * this execution fencing token.</p>
 */
@FunctionalInterface
public interface FencedTaskExecutionWork {
    void execute(TaskClaim claim, ClaimVisibility visibility, FencingToken executionFence) throws Exception;
}
