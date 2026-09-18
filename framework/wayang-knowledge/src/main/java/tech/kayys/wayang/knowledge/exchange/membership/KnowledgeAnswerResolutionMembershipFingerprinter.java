package tech.kayys.wayang.knowledge.exchange.membership;

/**
 * Defines the contract for knowledge answer resolution membership fingerprinter operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionMembershipFingerprinter {
    String fingerprint(KnowledgeAnswerResolutionMembershipSet membershipSet);
}
