
package tech.kayys.andalus.agent.kernel.interaction;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Ordered interaction event for an AgentRun.
 *
 * <p>This is intentionally protocol-neutral. WebSocket, SSE, A2UI,
 * gRPC, A2A, CLI, or another UI can project these events.</p>
 */
public record InteractionEvent(
        long sequence,
        String runId,
        InteractionEventType type,
        Instant timestamp,
        Map<String, Object> data
) {

    public InteractionEvent {
        if (sequence < 0) {
            throw new IllegalArgumentException(
                    "sequence must be >= 0"
            );
        }

        Objects.requireNonNull(
                runId,
                "runId"
        );

        Objects.requireNonNull(
                type,
                "type"
        );

        timestamp = timestamp == null
                ? Instant.now()
                : timestamp;

        data = data == null
                ? Map.of()
                : Map.copyOf(data);
    }
}
