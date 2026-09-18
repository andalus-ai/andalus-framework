package tech.kayys.wayang.knowledge.exchange.membership;

import java.util.List;

/**
 * Represents a knowledge answer resolution joint quorum decision.
 *
 * <p>Its components capture `old quorum reached`, `new quorum reached`, `old votes`, `new votes`, `old required`, and other values.</p>
 *
 * @param oldQuorumReached the old quorum reached
 * @param newQuorumReached the new quorum reached
 * @param oldVotes the old votes
 * @param newVotes the new votes
 * @param oldRequired the old required
 * @param newRequired the new required
 * @param oldAgreeingRuntimeIds the old agreeing runtime ids
 * @param newAgreeingRuntimeIds the new agreeing runtime ids
 */


public record KnowledgeAnswerResolutionJointQuorumDecision(
        boolean oldQuorumReached,
        boolean newQuorumReached,
        int oldVotes,
        int newVotes,
        int oldRequired,
        int newRequired,
        List<String> oldAgreeingRuntimeIds,
        List<String> newAgreeingRuntimeIds
) {
    public KnowledgeAnswerResolutionJointQuorumDecision {
        oldAgreeingRuntimeIds = oldAgreeingRuntimeIds == null ? List.of() : List.copyOf(oldAgreeingRuntimeIds);
        newAgreeingRuntimeIds = newAgreeingRuntimeIds == null ? List.of() : List.copyOf(newAgreeingRuntimeIds);
    }

    public boolean reached() {
        return oldQuorumReached && newQuorumReached;
    }
}
