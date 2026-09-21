package tech.kayys.andalus.knowledge.exchange.membership;

import tech.kayys.andalus.knowledge.exchange.attestation.KnowledgeAnswerResolutionVote;

import java.util.List;

/**
 * Defines the contract for knowledge answer resolution membership transition engine operations in the Andalus framework.
 */


public interface KnowledgeAnswerResolutionMembershipTransitionEngine {

    KnowledgeAnswerResolutionMembershipTransitionResult evaluate(
            KnowledgeAnswerResolutionMembershipTransition transition,
            KnowledgeAnswerResolutionJointMembership jointMembership,
            List<KnowledgeAnswerResolutionVote> votes
    );
}
