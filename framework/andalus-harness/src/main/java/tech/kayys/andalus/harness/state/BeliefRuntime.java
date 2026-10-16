package tech.kayys.andalus.harness.state;

import java.util.concurrent.CompletionStage;

/**
 * Primary runtime SPI for managing the lifecycle, revisions, and state snapshots of agent beliefs.
 */
public interface BeliefRuntime {

    /**
     * Applies an update request (create, revise, dispute, retract) to the execution's beliefs.
     *
     * @param request the update request with expected revision
     * @return completion stage resolving to update result
     */
    CompletionStage<BeliefUpdateResult> update(BeliefUpdateRequest request);

    /**
     * Produces an immutable {@link BeliefSnapshot} of the current working beliefs for an execution.
     *
     * @param executionId execution identifier
     * @return completion stage resolving to belief snapshot
     */
    CompletionStage<BeliefSnapshot> snapshot(String executionId);

    /**
     * Produces a full {@link AgentStateSnapshot} combining beliefs, unresolved questions, and references.
     *
     * @param executionId execution identifier
     * @return completion stage resolving to state snapshot
     */
    CompletionStage<AgentStateSnapshot> stateSnapshot(String executionId);
}
