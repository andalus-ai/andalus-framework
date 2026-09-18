package tech.kayys.wayang.knowledge.snapshot.pack;

import java.io.InputStream;

/**
 * Defines the contract for knowledge snapshot evidence package importer operations in the Wayang framework.
 */


public interface KnowledgeSnapshotEvidencePackageImporter {
    KnowledgeSnapshotEvidencePackage importPackage(InputStream input);
}
