package tech.kayys.andalus.harness.decision.trace;

import java.util.Map;
import java.util.Optional;

/**
 * Request to complete a decision trace.
 */
public record DecisionTraceCompletionRequest(
        String decisionTraceId,
        DecisionTraceStatus status,
        Optional<String> outcomeReference,
        Map<String, Object> metadata
) {
    public DecisionTraceCompletionRequest {
        if (decisionTraceId == null || decisionTraceId.isBlank()) {
            throw new IllegalArgumentException("decisionTraceId is required");
        }
        if (status == null) {
            status = DecisionTraceStatus.COMPLETED;
        }
        outcomeReference = outcomeReference == null ? Optional.empty() : outcomeReference;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static DecisionTraceCompletionRequest completed(String decisionTraceId, String outcomeReference) {
        return new DecisionTraceCompletionRequest(
                decisionTraceId,
                DecisionTraceStatus.COMPLETED,
                Optional.ofNullable(outcomeReference),
                Map.of()
        );
    }

    public static DecisionTraceCompletionRequest of(String decisionTraceId, DecisionTraceStatus status) {
        return new DecisionTraceCompletionRequest(
                decisionTraceId,
                status,
                Optional.empty(),
                Map.of()
        );
    }
}
