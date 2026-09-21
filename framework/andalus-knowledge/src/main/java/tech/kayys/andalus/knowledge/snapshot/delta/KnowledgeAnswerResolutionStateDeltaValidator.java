package tech.kayys.andalus.knowledge.snapshot.delta;

import tech.kayys.andalus.knowledge.exchange.statemachine.KnowledgeAnswerResolutionState;

/**
 * Defines the contract for knowledge answer resolution state delta validator operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionStateDeltaValidator {

    boolean validate(
            KnowledgeAnswerResolutionState source,
            KnowledgeAnswerResolutionStateDelta delta
    );
}
