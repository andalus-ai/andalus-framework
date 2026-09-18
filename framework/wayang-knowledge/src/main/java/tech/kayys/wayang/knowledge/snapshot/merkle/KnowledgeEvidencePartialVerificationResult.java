package tech.kayys.wayang.knowledge.snapshot.merkle;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge evidence partial verification result.
 *
 * <p>Its components capture `verified`, `root hash`, `leaf id`, `verified artifacts`, `issues`, and other values.</p>
 *
 * @param verified the verified
 * @param rootHash the root hash
 * @param leafId the leaf id
 * @param verifiedArtifacts the verified artifacts
 * @param issues the issues
 * @param metadata the metadata
 */


public record KnowledgeEvidencePartialVerificationResult(
        boolean verified,
        String rootHash,
        String leafId,
        List<String> verifiedArtifacts,
        List<String> issues,
        Map<String, String> metadata
) {
    public KnowledgeEvidencePartialVerificationResult {
        verifiedArtifacts = verifiedArtifacts == null ? List.of() : List.copyOf(verifiedArtifacts);
        issues = issues == null ? List.of() : List.copyOf(issues);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
