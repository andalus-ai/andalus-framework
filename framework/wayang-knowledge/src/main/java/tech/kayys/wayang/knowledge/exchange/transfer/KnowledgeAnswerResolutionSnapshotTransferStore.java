package tech.kayys.wayang.knowledge.exchange.transfer;

import java.util.Optional;

/**
 * Defines the contract for knowledge answer resolution snapshot transfer store operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionSnapshotTransferStore {

    void save(KnowledgeAnswerResolutionSnapshotTransferState state);

    Optional<KnowledgeAnswerResolutionSnapshotTransferState> get(String transferId);

    void delete(String transferId);
}
