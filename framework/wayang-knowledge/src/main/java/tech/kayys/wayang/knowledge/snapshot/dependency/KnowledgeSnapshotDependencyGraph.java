package tech.kayys.wayang.knowledge.snapshot.dependency;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.util.List;

/**
 * Defines the contract for knowledge snapshot dependency graph operations in the Wayang framework.
 */


public interface KnowledgeSnapshotDependencyGraph {

    void addDependency(KnowledgeSnapshotDependency dependency);

    void removeDependency(String dependencyId);

    List<KnowledgeSnapshotDependency> dependencies(KnowledgeSnapshotId snapshotId);

    List<KnowledgeSnapshotDependency> dependents(String targetId);

    List<KnowledgeSnapshotDependency> transitiveDependencies(KnowledgeSnapshotId snapshotId);
}
