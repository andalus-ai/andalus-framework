package tech.kayys.wayang.harness.governance.action;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Outcome of executing an authorized action through the governance layer.
 */
public record ActionExecutionResult(
        String actionId,
        boolean success,
        String output,
        Optional<String> errorMessage,
        Instant executedAt
) {

    public ActionExecutionResult {
        Objects.requireNonNull(actionId, "actionId");
        output = output == null ? "" : output;
        errorMessage = errorMessage == null ? Optional.empty() : errorMessage;
        executedAt = executedAt == null ? Instant.now() : executedAt;
    }

    public static ActionExecutionResult success(String actionId, String output) {
        return new ActionExecutionResult(actionId, true, output, Optional.empty(), Instant.now());
    }

    public static ActionExecutionResult failure(String actionId, String errorMessage) {
        return new ActionExecutionResult(actionId, false, "", Optional.ofNullable(errorMessage), Instant.now());
    }
}
