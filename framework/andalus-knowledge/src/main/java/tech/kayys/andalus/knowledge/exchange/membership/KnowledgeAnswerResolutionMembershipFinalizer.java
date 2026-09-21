package tech.kayys.andalus.knowledge.exchange.membership;

/**
 * Defines the contract for knowledge answer resolution membership finalizer operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionMembershipFinalizer {

    KnowledgeAnswerResolutionMembershipTransitionResult finalizeTransition(
            KnowledgeAnswerResolutionMembershipTransition transition,
            KnowledgeAnswerResolutionMembershipTransitionResult result
    );
}
