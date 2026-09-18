package tech.kayys.wayang.knowledge.snapshot;

/**
 * Defines the contract for knowledge snapshot aware operations in the Wayang framework.
 */


public interface KnowledgeSnapshotAware {

    String snapshotId();

    default boolean hasSnapshot() {
        return snapshotId() != null && !snapshotId().isBlank();
    }
}
