package tech.kayys.andalus.knowledge.seal;

/**
 * Defines the contract for knowledge snapshot signer operations in the Andalus framework.
 */


public interface KnowledgeSnapshotSigner {

    KnowledgeSnapshotSealAlgorithm algorithm();

    String keyId();

    String keyVersion();

    byte[] sign(byte[] payload);
}
