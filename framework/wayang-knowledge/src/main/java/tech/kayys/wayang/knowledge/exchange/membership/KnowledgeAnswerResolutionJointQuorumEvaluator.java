package tech.kayys.wayang.knowledge.exchange.membership;

import tech.kayys.wayang.knowledge.exchange.attestation.KnowledgeAnswerResolutionVote;

import java.util.List;

/**
 * Defines the contract for knowledge answer resolution joint quorum evaluator operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionJointQuorumEvaluator {

    KnowledgeAnswerResolutionJointQuorumDecision evaluate(
            KnowledgeAnswerResolutionJointMembership membership,
            List<KnowledgeAnswerResolutionVote> votes
    );
}
