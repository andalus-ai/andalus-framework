package tech.kayys.andalus.harness.decision.trace;

import java.util.Optional;

/**
 * Query for retrieving decision traces.
 */
public record DecisionTraceQuery(
        Optional<String> decisionTraceId,
        Optional<String> executionId,
        Optional<String> turnId
) {
    public DecisionTraceQuery {
        decisionTraceId = decisionTraceId == null ? Optional.empty() : decisionTraceId;
        executionId = executionId == null ? Optional.empty() : executionId;
        turnId = turnId == null ? Optional.empty() : turnId;
    }

    public static DecisionTraceQuery byTraceId(String decisionTraceId) {
        return new DecisionTraceQuery(Optional.ofNullable(decisionTraceId), Optional.empty(), Optional.empty());
    }

    public static DecisionTraceQuery byExecutionId(String executionId) {
        return new DecisionTraceQuery(Optional.empty(), Optional.ofNullable(executionId), Optional.empty());
    }

    public static DecisionTraceQuery byExecutionAndTurn(String executionId, String turnId) {
        return new DecisionTraceQuery(Optional.empty(), Optional.ofNullable(executionId), Optional.ofNullable(turnId));
    }
}
