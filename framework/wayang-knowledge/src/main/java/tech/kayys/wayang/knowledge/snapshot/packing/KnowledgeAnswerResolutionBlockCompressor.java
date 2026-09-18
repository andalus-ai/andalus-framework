package tech.kayys.wayang.knowledge.snapshot.packing;

/**
 * Defines the contract for knowledge answer resolution block compressor operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionBlockCompressor {

    String algorithm();

    byte[] compress(byte[] input);

    byte[] decompress(byte[] input);
}
