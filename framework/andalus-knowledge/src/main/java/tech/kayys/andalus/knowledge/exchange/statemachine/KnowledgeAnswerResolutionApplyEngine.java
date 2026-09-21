package tech.kayys.andalus.knowledge.exchange.statemachine;

import tech.kayys.andalus.knowledge.exchange.journal.KnowledgeAnswerResolutionLogEntry;

/**
 * Defines the contract for knowledge answer resolution apply engine operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionApplyEngine {

    void apply(KnowledgeAnswerResolutionLogEntry entry);

    long lastAppliedIndex();

    String stateFingerprint();

    KnowledgeAnswerResolutionState state();
}
