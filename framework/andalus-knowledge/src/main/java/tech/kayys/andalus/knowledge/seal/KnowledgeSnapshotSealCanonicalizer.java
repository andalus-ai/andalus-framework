package tech.kayys.andalus.knowledge.seal;

/**
 * Defines the contract for knowledge snapshot seal canonicalizer operations in the Andalus framework.
 */


public interface KnowledgeSnapshotSealCanonicalizer {

    byte[] canonicalize(KnowledgeSnapshotSealPayload payload);
}
