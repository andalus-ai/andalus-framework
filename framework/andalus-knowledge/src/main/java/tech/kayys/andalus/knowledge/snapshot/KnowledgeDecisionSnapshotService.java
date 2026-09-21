package tech.kayys.andalus.knowledge.snapshot;

import tech.kayys.andalus.knowledge.decision.KnowledgeDecisionTrace;

import java.util.Optional;

/**
 * Defines the contract for knowledge decision snapshot service operations in the Wayang framework.
 */


public interface KnowledgeDecisionSnapshotService {

    KnowledgeDecisionSnapshot capture(
            KnowledgeDecisionTrace trace,
            KnowledgeSnapshotCaptureContext context);

    Optional<KnowledgeDecisionSnapshot> get(String snapshotId);

    Optional<KnowledgeDecisionSnapshot> forTrace(String traceId);
}
