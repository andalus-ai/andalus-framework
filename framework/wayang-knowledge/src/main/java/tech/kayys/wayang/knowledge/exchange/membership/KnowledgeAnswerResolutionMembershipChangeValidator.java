package tech.kayys.wayang.knowledge.exchange.membership;

/**
 * Defines the contract for knowledge answer resolution membership change validator operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionMembershipChangeValidator {
    boolean validate(
            KnowledgeAnswerResolutionMembershipChange change,
            KnowledgeAnswerResolutionMembershipSet current);
}
