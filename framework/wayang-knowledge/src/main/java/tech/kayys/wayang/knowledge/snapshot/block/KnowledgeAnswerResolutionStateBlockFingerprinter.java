package tech.kayys.wayang.knowledge.snapshot.block;

/**
 * Defines the contract for knowledge answer resolution state block fingerprinter operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionStateBlockFingerprinter {
    String fingerprint(byte[] data);
}
