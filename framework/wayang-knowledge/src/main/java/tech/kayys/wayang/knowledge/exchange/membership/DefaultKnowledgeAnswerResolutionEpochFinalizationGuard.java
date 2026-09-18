package tech.kayys.wayang.knowledge.exchange.membership;

/**
 * Provides the default implementation of the knowledge answer resolution epoch finalization guard contract.
 */


public final class DefaultKnowledgeAnswerResolutionEpochFinalizationGuard
        implements KnowledgeAnswerResolutionEpochFinalizationGuard {

    @Override
    public boolean canFinalize(KnowledgeAnswerResolutionMembershipTransitionResult result) {
        if (result == null) {
            return false;
        }

        return result.state() == KnowledgeAnswerResolutionMembershipTransitionState.FINALIZING
                && result.oldQuorumReached()
                && result.newQuorumReached()
                && result.safeToFinalize();
    }
}
