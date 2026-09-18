package tech.kayys.wayang.knowledge.exchange.membership;

import tech.kayys.wayang.knowledge.exchange.attestation.KnowledgeAnswerResolutionVote;

import java.util.List;

/**
 * Defines the contract for knowledge answer resolution membership transition engine operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionMembershipTransitionEngine {

    KnowledgeAnswerResolutionMembershipTransitionResult evaluate(
            KnowledgeAnswerResolutionMembershipTransition transition,
            KnowledgeAnswerResolutionJointMembership jointMembership,
            List<KnowledgeAnswerResolutionVote> votes
    );
}
