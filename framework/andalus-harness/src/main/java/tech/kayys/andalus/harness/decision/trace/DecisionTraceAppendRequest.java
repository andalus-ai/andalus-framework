package tech.kayys.andalus.harness.decision.trace;

/**
 * Request to append an entry to an existing decision trace.
 */
public record DecisionTraceAppendRequest(
        String decisionTraceId,
        DecisionTraceEntry entry
) {
    public DecisionTraceAppendRequest {
        if (decisionTraceId == null || decisionTraceId.isBlank()) {
            throw new IllegalArgumentException("decisionTraceId is required");
        }
        if (entry == null) {
            throw new IllegalArgumentException("entry is required");
        }
    }
}
