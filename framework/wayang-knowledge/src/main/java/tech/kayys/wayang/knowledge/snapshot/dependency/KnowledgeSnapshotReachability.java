package tech.kayys.wayang.knowledge.snapshot.dependency;

import java.util.Set;

/**
 * Represents a knowledge snapshot reachability.
 *
 * <p>Its components capture `roots`, `reachable snapshots`, `unreachable snapshots`.</p>
 *
 * @param roots the roots
 * @param reachableSnapshots the reachable snapshots
 * @param unreachableSnapshots the unreachable snapshots
 */


public record KnowledgeSnapshotReachability(
        Set<String> roots,
        Set<String> reachableSnapshots,
        Set<String> unreachableSnapshots
) {

    public KnowledgeSnapshotReachability {
        roots = Set.copyOf(roots);
        reachableSnapshots = Set.copyOf(reachableSnapshots);
        unreachableSnapshots = Set.copyOf(unreachableSnapshots);
    }
}
