package tech.kayys.wayang.knowledge.integrity;

/**
 * Defines the contract for knowledge attestation generator operations in the Wayang framework.
 */


public interface KnowledgeAttestationGenerator {

    KnowledgeAttestation attest(KnowledgeSnapshotIntegrityResult result, String attesterId);
}
