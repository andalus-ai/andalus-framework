package tech.kayys.andalus.knowledge.integrity;

/**
 * Defines the contract for knowledge content fingerprint provider operations in the Wayang framework.
 */


public interface KnowledgeContentFingerprintProvider {

    String fingerprint(String knowledgeId, String versionId);
}
