package tech.kayys.andalus.knowledge.seal;

/**
 * Defines the contract for knowledge snapshot signature verifier operations in the Andalus framework.
 */


public interface KnowledgeSnapshotSignatureVerifier {

    boolean supports(KnowledgeSnapshotSealAlgorithm algorithm);

    boolean verify(byte[] payload, byte[] signature, String keyId, String keyVersion);
}
