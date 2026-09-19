package tech.kayys.wayang.spi.session;

import java.time.Instant;
import java.util.Set;

public record SessionQuery(
        String tenantId,
        String userId,
        String agentId,
        Set<SessionState> states,
        Instant createdAfter,
        Instant createdBefore,
        int limit) {

    public SessionQuery {
        states = states == null
                ? Set.of()
                : Set.copyOf(states);

        if (limit < 1 || limit > 1_000) {
            throw new IllegalArgumentException(
                    "limit must be between 1 and 1000");
        }
    }

    public static SessionQuery all() {
        return new SessionQuery(
                null,
                null,
                null,
                Set.of(),
                null,
                null,
                100);
    }
}
