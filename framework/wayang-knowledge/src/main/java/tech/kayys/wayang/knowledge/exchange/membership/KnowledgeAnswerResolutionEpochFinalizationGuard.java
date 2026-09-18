package tech.kayys.wayang.knowledge.exchange.membership;

/**
 * Defines the contract for knowledge answer resolution epoch finalization guard operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionEpochFinalizationGuard {
    boolean canFinalize(KnowledgeAnswerResolutionMembershipTransitionResult result);
}
