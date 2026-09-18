package tech.kayys.wayang.knowledge.snapshot.pack;

import java.util.Map;

/**
 * Represents a knowledge snapshot package verification context.
 *
 * <p>Its components capture `require integrity`, `require seal`, `require dependencies`, `require governance match`, `require policy match`, and other values.</p>
 *
 * @param requireIntegrity the require integrity
 * @param requireSeal the require seal
 * @param requireDependencies the require dependencies
 * @param requireGovernanceMatch the require governance match
 * @param requirePolicyMatch the require policy match
 * @param requireRuleMatch the require rule match
 * @param metadata the metadata
 */


public record KnowledgeSnapshotPackageVerificationContext(
        boolean requireIntegrity,
        boolean requireSeal,
        boolean requireDependencies,
        boolean requireGovernanceMatch,
        boolean requirePolicyMatch,
        boolean requireRuleMatch,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotPackageVerificationContext {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static KnowledgeSnapshotPackageVerificationContext strict() {
        return new KnowledgeSnapshotPackageVerificationContext(
                true, true, true, true, true, true, Map.of()
        );
    }

    public static KnowledgeSnapshotPackageVerificationContext relaxed() {
        return new KnowledgeSnapshotPackageVerificationContext(
                false, false, false, false, false, false, Map.of()
        );
    }
}
