package tech.kayys.wayang.knowledge.snapshot.delta;

import tech.kayys.wayang.knowledge.exchange.statemachine.KnowledgeAnswerResolutionState;

/**
 * Defines the contract for knowledge answer resolution state delta applier operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionStateDeltaApplier {

    KnowledgeAnswerResolutionState apply(
            KnowledgeAnswerResolutionState source,
            KnowledgeAnswerResolutionStateDelta delta
    );
}
