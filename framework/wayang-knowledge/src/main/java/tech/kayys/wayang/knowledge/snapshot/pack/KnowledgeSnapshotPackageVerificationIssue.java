package tech.kayys.wayang.knowledge.snapshot.pack;

import java.util.Map;

/**
 * Represents a knowledge snapshot package verification issue.
 *
 * <p>Its components capture `code`, `message`, `resource id`, `fatal`, `metadata`.</p>
 *
 * @param code the code
 * @param message the message
 * @param resourceId the resource id
 * @param fatal the fatal
 * @param metadata the metadata
 */


public record KnowledgeSnapshotPackageVerificationIssue(
        String code,
        String message,
        String resourceId,
        boolean fatal,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotPackageVerificationIssue {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
