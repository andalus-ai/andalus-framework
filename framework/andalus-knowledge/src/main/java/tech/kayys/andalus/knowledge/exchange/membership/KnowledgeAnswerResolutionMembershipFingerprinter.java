package tech.kayys.andalus.knowledge.exchange.membership;

/**
 * Defines the contract for knowledge answer resolution membership fingerprinter operations in the Andalus framework.
 */


public interface KnowledgeAnswerResolutionMembershipFingerprinter {
    String fingerprint(KnowledgeAnswerResolutionMembershipSet membershipSet);
}
