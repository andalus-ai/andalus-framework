package tech.kayys.wayang.spi.execution;

import java.time.Instant;
import java.util.Set;

public record ExecutionQuery(
        String tenantId,
        String userId,
        String agentId,
        String workflowId,
        Set<ExecutionState> states,
        Instant createdAfter,
        Instant createdBefore,
        int limit) {

    public ExecutionQuery {
        states = states == null
                ? Set.of()
                : Set.copyOf(states);

        if (limit < 1 || limit > 1_000) {
            throw new IllegalArgumentException(
                    "limit must be between 1 and 1000");
        }
    }

    public static ExecutionQuery all() {
        return new ExecutionQuery(
                null,
                null,
                null,
                null,
                Set.of(),
                null,
                null,
                100);
    }
}
