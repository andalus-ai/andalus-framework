package tech.kayys.wayang.knowledge.snapshot.dependency;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;
import tech.kayys.wayang.knowledge.snapshot.lifecycle.KnowledgeSnapshotDeletionDecision;

import java.util.List;
import java.util.Set;

/**
 * Represents a knowledge snapshot cascading retention decision.
 *
 * <p>Its components capture `snapshot id`, `decision`, `reachable snapshots`, `protected dependencies`, `reasons`.</p>
 *
 * @param snapshotId the snapshot id
 * @param decision the decision
 * @param reachableSnapshots the reachable snapshots
 * @param protectedDependencies the protected dependencies
 * @param reasons the reasons
 */


public record KnowledgeSnapshotCascadingRetentionDecision(
        KnowledgeSnapshotId snapshotId,
        KnowledgeSnapshotDeletionDecision decision,
        Set<String> reachableSnapshots,
        List<KnowledgeSnapshotDependency> protectedDependencies,
        List<String> reasons
) {

    public KnowledgeSnapshotCascadingRetentionDecision {
        reachableSnapshots = Set.copyOf(reachableSnapshots);
        protectedDependencies = List.copyOf(protectedDependencies);
        reasons = List.copyOf(reasons);
    }
}
