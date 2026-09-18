package tech.kayys.wayang.knowledge.exchange.membership;

/**
 * Provides the default implementation of the knowledge answer resolution membership change validator contract.
 */


public final class DefaultKnowledgeAnswerResolutionMembershipChangeValidator
        implements KnowledgeAnswerResolutionMembershipChangeValidator {

    @Override
    public boolean validate(
            KnowledgeAnswerResolutionMembershipChange change,
            KnowledgeAnswerResolutionMembershipSet current) {

        if (change == null || current == null) {
            return false;
        }

        if (!change.currentEpochId().equals(current.epochId())) {
            return false;
        }

        if (change.proposedRuntimeIds().isEmpty()) {
            return false;
        }

        if (change.proposedQuorum() < 1) {
            return false;
        }

        if (change.proposedQuorum() > change.proposedRuntimeIds().size()) {
            return false;
        }

        return true;
    }
}
