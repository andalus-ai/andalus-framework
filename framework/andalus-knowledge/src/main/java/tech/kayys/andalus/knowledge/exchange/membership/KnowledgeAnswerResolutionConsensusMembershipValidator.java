package tech.kayys.andalus.knowledge.exchange.membership;

import tech.kayys.andalus.knowledge.exchange.attestation.KnowledgeAnswerResolutionConsensusEpoch;
import tech.kayys.andalus.knowledge.exchange.attestation.KnowledgeAnswerResolutionConsensusProposal;

/**
 * Defines the contract for knowledge answer resolution consensus membership validator operations in the Andalus framework.
 */


public interface KnowledgeAnswerResolutionConsensusMembershipValidator {

    boolean validate(
            KnowledgeAnswerResolutionConsensusEpoch epoch,
            KnowledgeAnswerResolutionConsensusProposal proposal,
            KnowledgeAnswerResolutionMembershipSet membershipSet
    );
}
