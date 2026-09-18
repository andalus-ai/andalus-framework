package tech.kayys.wayang.knowledge.replay;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge replay snapshot.
 *
 * <p>Its components capture `trace id`, `execution id`, `agent id`, `operation`, `query`, and other values.</p>
 *
 * @param traceId the trace id
 * @param executionId the execution id
 * @param agentId the agent id
 * @param operation the operation
 * @param query the query
 * @param effectiveAt the effective at
 * @param evidenceIds the evidence ids
 * @param evidenceVersionIds the evidence version ids
 * @param lineageIds the lineage ids
 * @param policyIds the policy ids
 * @param ruleIds the rule ids
 * @param governanceFingerprint the governance fingerprint
 * @param policyFingerprint the policy fingerprint
 * @param evidenceFingerprint the evidence fingerprint
 * @param configurationFingerprint the configuration fingerprint
 * @param metadata the metadata
 */


public record KnowledgeReplaySnapshot(
        String traceId,
        String executionId,
        String agentId,
        String operation,
        String query,
        Instant effectiveAt,
        List<String> evidenceIds,
        List<String> evidenceVersionIds,
        List<String> lineageIds,
        List<String> policyIds,
        List<String> ruleIds,
        String governanceFingerprint,
        String policyFingerprint,
        String evidenceFingerprint,
        String configurationFingerprint,
        Map<String, Object> metadata
) {

    public KnowledgeReplaySnapshot {
        evidenceIds = evidenceIds == null ? List.of() : List.copyOf(evidenceIds);
        evidenceVersionIds = evidenceVersionIds == null ? List.of() : List.copyOf(evidenceVersionIds);
        lineageIds = lineageIds == null ? List.of() : List.copyOf(lineageIds);
        policyIds = policyIds == null ? List.of() : List.copyOf(policyIds);
        ruleIds = ruleIds == null ? List.of() : List.copyOf(ruleIds);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
