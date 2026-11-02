package tech.kayys.andalus.execution.attempt;

import tech.kayys.andalus.execution.mobility.FencingToken;

import java.util.List;
import java.util.Optional;

/**
 * Durable persistence port for physical execution attempts and their lineage.
 * Implementations must reject stale fencing generations and duplicate attempt IDs.
 */
public interface ExecutionAttemptStore {

    /** Persist a newly claimed attempt and its immutable lineage under the active fencing token. */
    void save(ExecutionAttempt attempt, AttemptLineage lineage, FencingToken fencingToken);

    /**
     * Update an existing attempt state under the same active fencing generation.
     * Implementations must enforce legal state transitions and reject stale tokens.
     */
    default void updateState(AttemptId attemptId, AttemptState newState, FencingToken fencingToken) {
        throw new UnsupportedOperationException("This attempt store does not support fenced state updates");
    }

    Optional<ExecutionAttempt> get(AttemptId attemptId);

    Optional<ExecutionAttempt> getLatest(String executionId);

    Optional<AttemptLineage> getLineage(AttemptId attemptId);

    List<ExecutionAttempt> list(String executionId);
}
