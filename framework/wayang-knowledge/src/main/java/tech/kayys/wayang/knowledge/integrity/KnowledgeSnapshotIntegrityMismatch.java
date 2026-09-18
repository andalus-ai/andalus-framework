package tech.kayys.wayang.knowledge.integrity;

import java.util.Map;

/**
 * Represents a knowledge snapshot integrity mismatch.
 *
 * <p>Its components capture `type`, `subject id`, `expected fingerprint`, `actual fingerprint`, `message`, and other values.</p>
 *
 * @param type the type
 * @param subjectId the subject id
 * @param expectedFingerprint the expected fingerprint
 * @param actualFingerprint the actual fingerprint
 * @param message the message
 * @param metadata the metadata
 */


public record KnowledgeSnapshotIntegrityMismatch(
        KnowledgeSnapshotIntegrityMismatchType type,
        String subjectId,
        String expectedFingerprint,
        String actualFingerprint,
        String message,
        Map<String, String> metadata
) {

    public KnowledgeSnapshotIntegrityMismatch {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
