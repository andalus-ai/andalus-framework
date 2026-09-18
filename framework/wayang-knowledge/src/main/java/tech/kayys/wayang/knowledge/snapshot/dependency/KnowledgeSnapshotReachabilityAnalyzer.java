package tech.kayys.wayang.knowledge.snapshot.dependency;

import java.time.Instant;

/**
 * Defines the contract for knowledge snapshot reachability analyzer operations in the Wayang framework.
 */


public interface KnowledgeSnapshotReachabilityAnalyzer {

    KnowledgeSnapshotReachability analyze(Instant now);
}
