package tech.kayys.wayang.knowledge.seal;

import tech.kayys.wayang.knowledge.integrity.KnowledgeSnapshotIntegrityResult;

/**
 * Defines the contract for knowledge snapshot secure seal service operations in the Wayang framework.
 */


public interface KnowledgeSnapshotSecureSealService {

    KnowledgeSnapshotSecureSeal seal(
            KnowledgeSnapshotIntegrityResult result,
            String verifierId,
            String verifierVersion
    );
}
