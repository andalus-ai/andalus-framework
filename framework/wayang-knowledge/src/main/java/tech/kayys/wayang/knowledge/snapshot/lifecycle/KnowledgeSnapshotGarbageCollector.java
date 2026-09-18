package tech.kayys.wayang.knowledge.snapshot.lifecycle;

import java.time.Instant;
import java.util.List;

/**
 * Defines the contract for knowledge snapshot garbage collector operations in the Wayang framework.
 */


public interface KnowledgeSnapshotGarbageCollector {

    List<KnowledgeSnapshotLifecycleDecision> collect(Instant now);
}
