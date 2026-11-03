package tech.kayys.andalus.harness.fabric;

import tech.kayys.andalus.execution.mobility.FencingToken;

/**
 * Completion port that receives the exact execution lease token held throughout work.
 * Implementations must independently validate the queue claim and assignment fence.
 */
@FunctionalInterface
public interface FencedExecutionCompletionCommitter {
    void commitCompleted(ExecutionAssignment assignment, TaskClaimToken claimToken,
                         FencingToken executionFence) throws Exception;
}
