package tech.kayys.wayang.knowledge.seal;

/**
 * Defines the contract for knowledge snapshot signature verifier operations in the Wayang framework.
 */


public interface KnowledgeSnapshotSignatureVerifier {

    boolean supports(KnowledgeSnapshotSealAlgorithm algorithm);

    boolean verify(byte[] payload, byte[] signature, String keyId, String keyVersion);
}
