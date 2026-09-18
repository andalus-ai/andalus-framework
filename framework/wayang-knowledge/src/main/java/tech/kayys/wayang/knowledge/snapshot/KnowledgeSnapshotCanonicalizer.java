package tech.kayys.wayang.knowledge.snapshot;

/**
 * Defines the contract for knowledge snapshot canonicalizer operations in the Wayang framework.
 */


public interface KnowledgeSnapshotCanonicalizer {

    String canonicalize(KnowledgeDecisionSnapshot snapshot);
}
