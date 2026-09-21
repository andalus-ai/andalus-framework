package tech.kayys.andalus.context.assembly;

import java.util.Objects;

/**
 * Request parameter for assembling context for an agent turn.
 */
public record ContextRequest(
        String sessionId,
        String query,
        ContextBudget budget) {

    public ContextRequest {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        query = query != null ? query : "";
        budget = budget != null ? budget : ContextBudget.standard();
    }
}
