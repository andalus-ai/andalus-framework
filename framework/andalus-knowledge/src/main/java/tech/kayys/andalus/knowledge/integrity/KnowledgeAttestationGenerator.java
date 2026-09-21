package tech.kayys.andalus.knowledge.integrity;

/**
 * Defines the contract for knowledge attestation generator operations in the Andalus framework.
 */


public interface KnowledgeAttestationGenerator {

    KnowledgeAttestation attest(KnowledgeSnapshotIntegrityResult result, String attesterId);
}
