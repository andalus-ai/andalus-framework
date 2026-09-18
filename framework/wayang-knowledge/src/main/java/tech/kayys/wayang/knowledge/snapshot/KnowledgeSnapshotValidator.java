package tech.kayys.wayang.knowledge.snapshot;

/**
 * Defines the contract for knowledge snapshot validator operations in the Wayang framework.
 */


public interface KnowledgeSnapshotValidator {

    KnowledgeSnapshotValidation validate(KnowledgeDecisionSnapshot snapshot);
}
