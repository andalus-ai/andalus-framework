package tech.kayys.wayang.knowledge.exchange.checkpoint;

import tech.kayys.wayang.knowledge.exchange.statemachine.KnowledgeAnswerResolutionState;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a knowledge answer resolution state checkpoint.
 *
 * <p>Its components capture `last applied index`, `term`, `epoch id`, `state fingerprint`, `state`, and other values.</p>
 *
 * @param lastAppliedIndex the last applied index
 * @param term the term
 * @param epochId the epoch id
 * @param stateFingerprint the state fingerprint
 * @param state the state
 * @param createdAt the created at
 * @param status the status
 */


public record KnowledgeAnswerResolutionStateCheckpoint(
        long lastAppliedIndex,
        long term,
        String epochId,
        String stateFingerprint,
        KnowledgeAnswerResolutionState state,
        Instant createdAt,
        KnowledgeAnswerResolutionCheckpointStatus status
) {
    public KnowledgeAnswerResolutionStateCheckpoint {
        Objects.requireNonNull(stateFingerprint, "stateFingerprint");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(status, "status");

        if (lastAppliedIndex < -1) {
            throw new IllegalArgumentException("lastAppliedIndex must be >= -1");
        }
        if (term < 0) {
            throw new IllegalArgumentException("term must be >= 0");
        }
    }
}
