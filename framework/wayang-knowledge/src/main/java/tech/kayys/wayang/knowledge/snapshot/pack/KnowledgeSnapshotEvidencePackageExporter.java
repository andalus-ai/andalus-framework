package tech.kayys.wayang.knowledge.snapshot.pack;

import java.io.OutputStream;

/**
 * Defines the contract for knowledge snapshot evidence package exporter operations in the Wayang framework.
 */


public interface KnowledgeSnapshotEvidencePackageExporter {
    void export(
            KnowledgeSnapshotEvidencePackage evidencePackage,
            OutputStream output
    );
}
