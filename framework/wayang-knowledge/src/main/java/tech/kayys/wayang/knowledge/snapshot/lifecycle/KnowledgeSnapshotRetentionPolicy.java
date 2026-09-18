package tech.kayys.wayang.knowledge.snapshot.lifecycle;

import java.time.Duration;
import java.util.Map;

/**
 * Represents a knowledge snapshot retention policy.
 *
 * <p>Its components capture `retention periods`, `allow automatic deletion`, `archive before delete`, `require no references`, `require no holds`.</p>
 *
 * @param retentionPeriods the retention periods
 * @param allowAutomaticDeletion the allow automatic deletion
 * @param archiveBeforeDelete the archive before delete
 * @param requireNoReferences the require no references
 * @param requireNoHolds the require no holds
 */


public record KnowledgeSnapshotRetentionPolicy(
        Map<KnowledgeSnapshotRetentionClass, Duration> retentionPeriods,
        boolean allowAutomaticDeletion,
        boolean archiveBeforeDelete,
        boolean requireNoReferences,
        boolean requireNoHolds
) {

    public KnowledgeSnapshotRetentionPolicy {
        retentionPeriods = retentionPeriods == null ? Map.of() : Map.copyOf(retentionPeriods);
    }

    public Duration retentionFor(KnowledgeSnapshotRetentionClass retentionClass) {
        return retentionPeriods.get(retentionClass);
    }

    public static KnowledgeSnapshotRetentionPolicy defaults() {
        return new KnowledgeSnapshotRetentionPolicy(
                Map.of(
                        KnowledgeSnapshotRetentionClass.TRANSIENT, Duration.ofHours(1),
                        KnowledgeSnapshotRetentionClass.STANDARD, Duration.ofDays(30),
                        KnowledgeSnapshotRetentionClass.AUDIT, Duration.ofDays(365),
                        KnowledgeSnapshotRetentionClass.ARCHIVAL, Duration.ofDays(3650)
                ),
                true,
                true,
                true,
                true
        );
    }
}
