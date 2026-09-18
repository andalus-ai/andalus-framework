package tech.kayys.wayang.knowledge.exchange.membership;

/**
 * Defines the contract for knowledge answer resolution membership finalizer operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionMembershipFinalizer {

    KnowledgeAnswerResolutionMembershipTransitionResult finalizeTransition(
            KnowledgeAnswerResolutionMembershipTransition transition,
            KnowledgeAnswerResolutionMembershipTransitionResult result
    );
}
