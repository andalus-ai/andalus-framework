package tech.kayys.wayang.knowledge.snapshot.packing;

/**
 * Defines the contract for knowledge answer resolution block unpacker operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionBlockUnpacker {

    byte[] unpack(KnowledgeAnswerResolutionPackedStateBlock packedBlock);
}
