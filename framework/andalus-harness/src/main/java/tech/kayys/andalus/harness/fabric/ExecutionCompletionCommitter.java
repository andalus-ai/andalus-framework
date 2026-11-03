package tech.kayys.andalus.harness.fabric;

/**
 * Durable completion boundary between worker execution and queue acknowledgement.
 *
 * <p>An implementation must atomically persist the terminal execution state and
 * physical attempt outcome, or provide a durable reconciliation record for partial
 * writes across stores. It must reject a stale assignment fence and must verify that
 * the claim token still represents the worker's current queue claim before committing.
 * The queue acknowledgement is performed by {@link WorkerTaskPoller} only after this
 * method returns successfully.</p>
 */
@FunctionalInterface
public interface ExecutionCompletionCommitter {
    void commitCompleted(ExecutionAssignment assignment, TaskClaimToken claimToken) throws Exception;
}
