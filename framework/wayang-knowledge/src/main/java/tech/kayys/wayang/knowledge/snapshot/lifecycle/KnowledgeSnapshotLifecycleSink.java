package tech.kayys.wayang.knowledge.snapshot.lifecycle;

/**
 * Defines the contract for knowledge snapshot lifecycle sink operations in the Wayang framework.
 */


public interface KnowledgeSnapshotLifecycleSink {

    void publish(KnowledgeSnapshotLifecycleEvent event);
}
