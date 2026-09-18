package tech.kayys.wayang.knowledge.exchange.statemachine;

import tech.kayys.wayang.knowledge.exchange.journal.KnowledgeAnswerResolutionLogEntry;

/**
 * Defines the contract for knowledge answer resolution state machine operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionStateMachine {

    void apply(KnowledgeAnswerResolutionLogEntry entry);

    KnowledgeAnswerResolutionState state();

    String stateFingerprint();

    void reset();
}
