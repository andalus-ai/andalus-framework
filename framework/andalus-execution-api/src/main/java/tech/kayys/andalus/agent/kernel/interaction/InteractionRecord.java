
package tech.kayys.andalus.agent.kernel.interaction;

import java.time.Instant;
import java.util.Map;

public record InteractionRecord(
        long sequence,
        String requestId,
        String runId,
        InteractionRequestType type,
        InteractionEventType eventType,
        InteractionCorrelation correlation,
        Instant timestamp,
        Map<String, Object> data
) {

    public InteractionRecord {
        data = data == null
                ? Map.of()
                : Map.copyOf(data);
    }
}
