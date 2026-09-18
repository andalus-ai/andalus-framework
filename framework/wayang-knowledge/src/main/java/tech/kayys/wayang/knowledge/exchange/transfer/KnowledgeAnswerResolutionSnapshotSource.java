package tech.kayys.wayang.knowledge.exchange.transfer;

/**
 * Defines the contract for knowledge answer resolution snapshot source operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionSnapshotSource {

    KnowledgeAnswerResolutionSnapshotDescriptor descriptor(String snapshotId);

    KnowledgeAnswerResolutionSnapshotTransferChunk read(
            KnowledgeAnswerResolutionSnapshotTransferRequest request);
}
