package tech.kayys.wayang.knowledge.snapshot.delta;

import tech.kayys.wayang.knowledge.exchange.statemachine.KnowledgeAnswerResolutionState;

/**
 * Defines the contract for knowledge answer resolution state delta builder operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionStateDeltaBuilder {

    KnowledgeAnswerResolutionStateDelta build(
            KnowledgeAnswerResolutionState source,
            KnowledgeAnswerResolutionState target,
            String sourceSnapshotId,
            String targetSnapshotId,
            String tenantId,
            String sourceEpochId,
            String targetEpochId
    );
}
