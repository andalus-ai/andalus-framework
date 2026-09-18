package tech.kayys.wayang.knowledge.snapshot.lifecycle;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.util.List;

/**
 * Represents a knowledge snapshot lifecycle decision.
 *
 * <p>Its components capture `snapshot id`, `decision`, `current state`, `active references`, `active holds`, and other values.</p>
 *
 * @param snapshotId the snapshot id
 * @param decision the decision
 * @param currentState the current state
 * @param activeReferences the active references
 * @param activeHolds the active holds
 * @param reasons the reasons
 */


public record KnowledgeSnapshotLifecycleDecision(
        KnowledgeSnapshotId snapshotId,
        KnowledgeSnapshotDeletionDecision decision,
        KnowledgeSnapshotLifecycleState currentState,
        List<KnowledgeSnapshotReference> activeReferences,
        List<KnowledgeSnapshotHold> activeHolds,
        List<String> reasons
) {

    public KnowledgeSnapshotLifecycleDecision {
        activeReferences = activeReferences == null ? List.of() : List.copyOf(activeReferences);
        activeHolds = activeHolds == null ? List.of() : List.copyOf(activeHolds);
        reasons = reasons == null ? List.of() : List.copyOf(reasons);
    }
}
