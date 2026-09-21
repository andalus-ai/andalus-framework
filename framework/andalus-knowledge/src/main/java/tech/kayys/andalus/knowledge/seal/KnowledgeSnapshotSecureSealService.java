package tech.kayys.andalus.knowledge.seal;

import tech.kayys.andalus.knowledge.integrity.KnowledgeSnapshotIntegrityResult;

/**
 * Defines the contract for knowledge snapshot secure seal service operations in the Andalus framework.
 */


public interface KnowledgeSnapshotSecureSealService {

    KnowledgeSnapshotSecureSeal seal(
            KnowledgeSnapshotIntegrityResult result,
            String verifierId,
            String verifierVersion
    );
}
