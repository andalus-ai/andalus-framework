package tech.kayys.wayang.knowledge.exchange.membership;

import java.time.Instant;
import java.util.List;

/**
 * Represents a knowledge answer resolution membership change.
 *
 * <p>Its components capture `change id`, `current epoch id`, `type`, `current runtime ids`, `proposed runtime ids`, and other values.</p>
 *
 * @param changeId the change id
 * @param currentEpochId the current epoch id
 * @param type the type
 * @param currentRuntimeIds the current runtime ids
 * @param proposedRuntimeIds the proposed runtime ids
 * @param proposedQuorum the proposed quorum
 * @param proposerRuntimeId the proposer runtime id
 * @param proposedAt the proposed at
 * @param effectiveAt the effective at
 * @param reason the reason
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionMembershipChange(
        String changeId,
        String currentEpochId,
        KnowledgeAnswerResolutionMembershipChangeType type,
        List<String> currentRuntimeIds,
        List<String> proposedRuntimeIds,
        int proposedQuorum,
        String proposerRuntimeId,
        Instant proposedAt,
        Instant effectiveAt,
        String reason,
        String metadata
) {
    public KnowledgeAnswerResolutionMembershipChange {
        currentRuntimeIds = currentRuntimeIds == null ? List.of() : List.copyOf(currentRuntimeIds);
        proposedRuntimeIds = proposedRuntimeIds == null ? List.of() : List.copyOf(proposedRuntimeIds);
    }
}
