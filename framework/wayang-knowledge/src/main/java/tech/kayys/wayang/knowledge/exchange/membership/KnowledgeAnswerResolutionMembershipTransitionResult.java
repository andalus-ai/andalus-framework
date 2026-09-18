package tech.kayys.wayang.knowledge.exchange.membership;

import java.time.Instant;
import java.util.List;

/**
 * Represents a knowledge answer resolution membership transition result.
 *
 * <p>Its components capture `state`, `transition id`, `old epoch id`, `new epoch id`, `old quorum reached`, and other values.</p>
 *
 * @param state the state
 * @param transitionId the transition id
 * @param oldEpochId the old epoch id
 * @param newEpochId the new epoch id
 * @param oldQuorumReached the old quorum reached
 * @param newQuorumReached the new quorum reached
 * @param safeToFinalize the safe to finalize
 * @param decidedAt the decided at
 * @param diagnostics the diagnostics
 */


public record KnowledgeAnswerResolutionMembershipTransitionResult(
        KnowledgeAnswerResolutionMembershipTransitionState state,
        String transitionId,
        String oldEpochId,
        String newEpochId,
        boolean oldQuorumReached,
        boolean newQuorumReached,
        boolean safeToFinalize,
        Instant decidedAt,
        List<String> diagnostics
) {
    public KnowledgeAnswerResolutionMembershipTransitionResult {
        diagnostics = diagnostics == null ? List.of() : List.copyOf(diagnostics);
    }
}
