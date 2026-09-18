package tech.kayys.wayang.knowledge.exchange.membership;

import java.time.Instant;
import java.util.List;

/**
 * Represents a knowledge answer resolution membership transition.
 *
 * <p>Its components capture `transition id`, `type`, `old epoch id`, `proposed epoch id`, `old runtime ids`, and other values.</p>
 *
 * @param transitionId the transition id
 * @param type the type
 * @param oldEpochId the old epoch id
 * @param proposedEpochId the proposed epoch id
 * @param oldRuntimeIds the old runtime ids
 * @param newRuntimeIds the new runtime ids
 * @param oldQuorum the old quorum
 * @param newQuorum the new quorum
 * @param proposerRuntimeId the proposer runtime id
 * @param proposedAt the proposed at
 * @param effectiveAt the effective at
 * @param reason the reason
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionMembershipTransition(
        String transitionId,
        KnowledgeAnswerResolutionMembershipChangeType type,
        String oldEpochId,
        String proposedEpochId,
        List<String> oldRuntimeIds,
        List<String> newRuntimeIds,
        int oldQuorum,
        int newQuorum,
        String proposerRuntimeId,
        Instant proposedAt,
        Instant effectiveAt,
        String reason,
        String metadata
) {
    public KnowledgeAnswerResolutionMembershipTransition {
        oldRuntimeIds = oldRuntimeIds == null ? List.of() : List.copyOf(oldRuntimeIds);
        newRuntimeIds = newRuntimeIds == null ? List.of() : List.copyOf(newRuntimeIds);
    }
}
