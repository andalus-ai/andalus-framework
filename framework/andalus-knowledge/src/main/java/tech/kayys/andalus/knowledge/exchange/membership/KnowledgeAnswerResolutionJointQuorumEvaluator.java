package tech.kayys.andalus.knowledge.exchange.membership;

import tech.kayys.andalus.knowledge.exchange.attestation.KnowledgeAnswerResolutionVote;

import java.util.List;

/**
 * Defines the contract for knowledge answer resolution joint quorum evaluator operations in the Andalus framework.
 */


public interface KnowledgeAnswerResolutionJointQuorumEvaluator {

    KnowledgeAnswerResolutionJointQuorumDecision evaluate(
            KnowledgeAnswerResolutionJointMembership membership,
            List<KnowledgeAnswerResolutionVote> votes
    );
}
