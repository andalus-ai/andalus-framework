package tech.kayys.wayang.knowledge.exchange.transfer;

import tech.kayys.wayang.knowledge.exchange.checkpoint.KnowledgeAnswerResolutionStateCheckpoint;

/**
 * Defines the contract for knowledge answer resolution snapshot sink operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionSnapshotSink {

    void begin(KnowledgeAnswerResolutionSnapshotDescriptor descriptor);

    void write(KnowledgeAnswerResolutionSnapshotTransferChunk chunk);

    KnowledgeAnswerResolutionStateCheckpoint complete();

    void abort();
}
