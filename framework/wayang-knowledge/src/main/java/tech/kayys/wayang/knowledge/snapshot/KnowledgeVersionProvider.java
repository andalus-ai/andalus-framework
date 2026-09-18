package tech.kayys.wayang.knowledge.snapshot;

/**
 * Defines the contract for knowledge version provider operations in the Wayang framework.
 */


public interface KnowledgeVersionProvider {

    KnowledgeVersionReference resolve(String id, String versionId);
}
