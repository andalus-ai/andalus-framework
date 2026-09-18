package tech.kayys.wayang.knowledge.snapshot.pack;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge snapshot evidence package.
 *
 * <p>Its components capture `package id`, `manifest`, `instructions`, `resources`, `metadata`.</p>
 *
 * @param packageId the package id
 * @param manifest the manifest
 * @param instructions the instructions
 * @param resources the resources
 * @param metadata the metadata
 */


public record KnowledgeSnapshotEvidencePackage(
        String packageId,
        KnowledgeSnapshotVerificationManifest manifest,
        KnowledgeSnapshotVerificationInstructions instructions,
        List<KnowledgeSnapshotPackageResource> resources,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotEvidencePackage {
        resources = resources == null ? List.of() : List.copyOf(resources);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public KnowledgeSnapshotEvidencePackage(
            String packageId,
            KnowledgeSnapshotVerificationManifest manifest,
            List<KnowledgeSnapshotPackageResource> resources,
            Map<String, String> metadata
    ) {
        this(packageId, manifest, null, resources, metadata);
    }
}
