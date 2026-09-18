package tech.kayys.wayang.knowledge.seal;

/**
 * Defines the contract for knowledge snapshot signer operations in the Wayang framework.
 */


public interface KnowledgeSnapshotSigner {

    KnowledgeSnapshotSealAlgorithm algorithm();

    String keyId();

    String keyVersion();

    byte[] sign(byte[] payload);
}
