package tech.kayys.wayang.knowledge.exchange.statemachine;

/**
 * Defines the contract for knowledge answer resolution state fingerprinter operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionStateFingerprinter {
    String fingerprint(KnowledgeAnswerResolutionState state);
}
