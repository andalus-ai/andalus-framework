package tech.kayys.andalus.knowledge.integrity;

import tech.kayys.andalus.knowledge.snapshot.KnowledgeGovernanceSnapshot;
import tech.kayys.andalus.knowledge.snapshot.KnowledgeRuntimeSnapshot;

/**
 * Defines the contract for knowledge integrity fingerprint provider operations in the Wayang framework.
 */


public interface KnowledgeIntegrityFingerprintProvider {

    String fingerprintKnowledge(String knowledgeId, String versionId);

    String fingerprintPolicy(String policyId, String versionId);

    String fingerprintRule(String ruleId, String versionId);

    String fingerprintGovernance(KnowledgeGovernanceSnapshot governance);

    String fingerprintRuntime(KnowledgeRuntimeSnapshot runtime);

    String fingerprintLineage(String lineageId);
}
