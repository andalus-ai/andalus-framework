package tech.kayys.andalus.knowledge.seal;

/**
 * Defines the knowledge snapshot seal algorithm values used by the Andalus framework.
 */


public enum KnowledgeSnapshotSealAlgorithm {
    SHA256_DIGEST,
    ED25519,
    ECDSA_SHA256,
    RSA_SHA256,
    EXTERNAL
}
