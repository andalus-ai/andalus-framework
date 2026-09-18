package tech.kayys.wayang.knowledge.exchange.statemachine;

/**
 * Defines the contract for knowledge answer resolution state canonicalizer operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionStateCanonicalizer {
    String canonicalize(KnowledgeAnswerResolutionState state);
}
