package tech.kayys.andalus.knowledge.integrity;

import tech.kayys.andalus.knowledge.snapshot.KnowledgeSnapshotId;

import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for knowledge attestation store operations in the Andalus framework.
 */


public interface KnowledgeAttestationStore {

    void save(KnowledgeAttestation attestation);

    Optional<KnowledgeAttestation> get(String attestationId);

    List<KnowledgeAttestation> findBySnapshot(KnowledgeSnapshotId snapshotId);
}
