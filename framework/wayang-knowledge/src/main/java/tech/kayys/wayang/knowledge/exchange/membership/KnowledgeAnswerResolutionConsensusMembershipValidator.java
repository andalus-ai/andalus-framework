package tech.kayys.wayang.knowledge.exchange.membership;

import tech.kayys.wayang.knowledge.exchange.attestation.KnowledgeAnswerResolutionConsensusEpoch;
import tech.kayys.wayang.knowledge.exchange.attestation.KnowledgeAnswerResolutionConsensusProposal;

/**
 * Defines the contract for knowledge answer resolution consensus membership validator operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionConsensusMembershipValidator {

    boolean validate(
            KnowledgeAnswerResolutionConsensusEpoch epoch,
            KnowledgeAnswerResolutionConsensusProposal proposal,
            KnowledgeAnswerResolutionMembershipSet membershipSet
    );
}
