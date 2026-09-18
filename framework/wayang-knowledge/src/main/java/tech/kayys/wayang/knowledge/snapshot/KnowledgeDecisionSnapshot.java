package tech.kayys.wayang.knowledge.snapshot;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge decision snapshot.
 *
 * <p>Its components capture `snapshot id`, `execution id`, `trace id`, `agent id`, `operation`, and other values.</p>
 *
 * @param snapshotId the snapshot id
 * @param executionId the execution id
 * @param traceId the trace id
 * @param agentId the agent id
 * @param operation the operation
 * @param query the query
 * @param effectiveAt the effective at
 * @param knowledge the knowledge
 * @param policies the policies
 * @param rules the rules
 * @param governance the governance
 * @param runtime the runtime
 * @param aggregateFingerprint the aggregate fingerprint
 * @param createdAt the created at
 * @param metadata the metadata
 */


public record KnowledgeDecisionSnapshot(
        KnowledgeSnapshotId snapshotId,
        String executionId,
        String traceId,
        String agentId,
        String operation,
        String query,
        Instant effectiveAt,
        List<KnowledgeSnapshotEntry> knowledge,
        KnowledgePolicySnapshot policies,
        KnowledgeRuleSnapshot rules,
        KnowledgeGovernanceSnapshot governance,
        KnowledgeRuntimeSnapshot runtime,
        String aggregateFingerprint,
        Instant createdAt,
        Map<String, Object> metadata
) {

    public KnowledgeDecisionSnapshot {
        knowledge = knowledge == null ? List.of() : List.copyOf(knowledge);
        policies = policies == null ? new KnowledgePolicySnapshot(List.of(), "", Map.of()) : policies;
        rules = rules == null ? new KnowledgeRuleSnapshot(List.of(), "", Map.of()) : rules;
        governance = governance == null ? new KnowledgeGovernanceSnapshot(null, null, null, null, null, null, null, Map.of()) : governance;
        runtime = runtime == null ? new KnowledgeRuntimeSnapshot(null, null, null, null, null, null, null, null, Map.of()) : runtime;
        aggregateFingerprint = aggregateFingerprint == null ? "" : aggregateFingerprint;
        createdAt = createdAt == null ? Instant.now() : createdAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
