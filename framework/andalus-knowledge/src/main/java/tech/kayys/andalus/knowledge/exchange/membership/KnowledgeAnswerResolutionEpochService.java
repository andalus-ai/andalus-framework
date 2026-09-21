package tech.kayys.andalus.knowledge.exchange.membership;

import tech.kayys.andalus.knowledge.exchange.attestation.KnowledgeAnswerResolutionConsensusEpoch;

import java.time.Instant;

/**
 * Defines the contract for knowledge answer resolution epoch service operations in the Andalus framework.
 */


public interface KnowledgeAnswerResolutionEpochService {

    KnowledgeAnswerResolutionConsensusEpoch create(
            KnowledgeAnswerResolutionMembershipSet membershipSet,
            KnowledgeAnswerResolutionConsensusEpoch previous,
            Instant now
    );

    boolean validate(
            KnowledgeAnswerResolutionConsensusEpoch epoch,
            KnowledgeAnswerResolutionMembershipSet membershipSet,
            Instant now
    );
}
