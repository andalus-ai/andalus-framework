package tech.kayys.wayang.knowledge.snapshot.dependency;

import java.time.Instant;
import java.util.List;

/**
 * Defines the contract for knowledge snapshot cascading garbage collector operations in the Wayang framework.
 */


public interface KnowledgeSnapshotCascadingGarbageCollector {

    List<KnowledgeSnapshotCascadingRetentionDecision> collect(Instant now);
}
