package tech.kayys.wayang.knowledge.seal;

/**
 * Defines the contract for knowledge snapshot seal canonicalizer operations in the Wayang framework.
 */


public interface KnowledgeSnapshotSealCanonicalizer {

    byte[] canonicalize(KnowledgeSnapshotSealPayload payload);
}
