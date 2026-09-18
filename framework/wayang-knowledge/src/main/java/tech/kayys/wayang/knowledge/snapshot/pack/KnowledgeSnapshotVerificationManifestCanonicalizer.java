package tech.kayys.wayang.knowledge.snapshot.pack;

/**
 * Defines the contract for knowledge snapshot verification manifest canonicalizer operations in the Wayang framework.
 */


public interface KnowledgeSnapshotVerificationManifestCanonicalizer {
    String canonicalize(KnowledgeSnapshotVerificationManifest manifest);
}
