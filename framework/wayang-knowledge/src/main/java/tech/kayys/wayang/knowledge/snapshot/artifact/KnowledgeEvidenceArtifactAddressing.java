package tech.kayys.wayang.knowledge.snapshot.artifact;

/**
 * Defines the contract for knowledge evidence artifact addressing operations in the Wayang framework.
 */


public interface KnowledgeEvidenceArtifactAddressing {
    KnowledgeEvidenceArtifactId identify(byte[] content);
    boolean matches(KnowledgeEvidenceArtifactId id, byte[] content);
}
