package tech.kayys.wayang.knowledge.replay;

import tech.kayys.wayang.knowledge.decision.KnowledgeDecisionTrace;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge replay result.
 *
 * <p>Its components capture `replay id`, `trace id`, `status`, `original trace`, `replayed trace`, and other values.</p>
 *
 * @param replayId the replay id
 * @param traceId the trace id
 * @param status the status
 * @param originalTrace the original trace
 * @param replayedTrace the replayed trace
 * @param divergences the divergences
 * @param diagnostics the diagnostics
 */


public record KnowledgeReplayResult(
        String replayId,
        String traceId,
        KnowledgeReplayStatus status,
        KnowledgeDecisionTrace originalTrace,
        KnowledgeDecisionTrace replayedTrace,
        List<String> divergences,
        Map<String, Object> diagnostics
) {

    public KnowledgeReplayResult {
        divergences = divergences == null ? List.of() : List.copyOf(divergences);
        diagnostics = diagnostics == null ? Map.of() : Map.copyOf(diagnostics);
    }

    public boolean reproduced() {
        return status == KnowledgeReplayStatus.REPRODUCED;
    }

    public boolean diverged() {
        return status == KnowledgeReplayStatus.DIVERGED;
    }
}
