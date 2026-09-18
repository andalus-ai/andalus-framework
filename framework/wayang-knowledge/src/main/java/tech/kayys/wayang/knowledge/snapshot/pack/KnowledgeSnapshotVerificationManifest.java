package tech.kayys.wayang.knowledge.snapshot.pack;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge snapshot verification manifest.
 *
 * <p>Its components capture `manifest id`, `snapshot id`, `snapshot fingerprint`, `schema version`, `runtime version`, and other values.</p>
 *
 * @param manifestId the manifest id
 * @param snapshotId the snapshot id
 * @param snapshotFingerprint the snapshot fingerprint
 * @param schemaVersion the schema version
 * @param runtimeVersion the runtime version
 * @param knowledgeEngineVersion the knowledge engine version
 * @param evidence the evidence
 * @param lineage the lineage
 * @param policies the policies
 * @param rules the rules
 * @param governance the governance
 * @param integrity the integrity
 * @param seal the seal
 * @param dependencies the dependencies
 * @param merkle the merkle
 * @param createdAt the created at
 * @param metadata the metadata
 */


public record KnowledgeSnapshotVerificationManifest(
        String manifestId,
        String snapshotId,
        String snapshotFingerprint,
        String schemaVersion,
        String runtimeVersion,
        String knowledgeEngineVersion,
        List<KnowledgeSnapshotEvidenceEntry> evidence,
        List<KnowledgeSnapshotLineageEntry> lineage,
        List<KnowledgeSnapshotReferenceEntry> policies,
        List<KnowledgeSnapshotReferenceEntry> rules,
        KnowledgeSnapshotGovernanceManifest governance,
        KnowledgeSnapshotIntegrityManifest integrity,
        KnowledgeSnapshotSealManifest seal,
        List<KnowledgeSnapshotDependencyEntry> dependencies,
        KnowledgeSnapshotMerkleManifest merkle,
        Instant createdAt,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotVerificationManifest {
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        lineage = lineage == null ? List.of() : List.copyOf(lineage);
        policies = policies == null ? List.of() : List.copyOf(policies);
        rules = rules == null ? List.of() : List.copyOf(rules);
        dependencies = dependencies == null ? List.of() : List.copyOf(dependencies);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public KnowledgeSnapshotVerificationManifest(
            String manifestId,
            String snapshotId,
            String snapshotFingerprint,
            String schemaVersion,
            String runtimeVersion,
            String knowledgeEngineVersion,
            List<KnowledgeSnapshotEvidenceEntry> evidence,
            List<KnowledgeSnapshotLineageEntry> lineage,
            List<KnowledgeSnapshotReferenceEntry> policies,
            List<KnowledgeSnapshotReferenceEntry> rules,
            KnowledgeSnapshotGovernanceManifest governance,
            KnowledgeSnapshotIntegrityManifest integrity,
            KnowledgeSnapshotSealManifest seal,
            List<KnowledgeSnapshotDependencyEntry> dependencies,
            Instant createdAt,
            Map<String, String> metadata
    ) {
        this(manifestId, snapshotId, snapshotFingerprint, schemaVersion, runtimeVersion, knowledgeEngineVersion,
                evidence, lineage, policies, rules, governance, integrity, seal, dependencies, null, createdAt, metadata);
    }
}
