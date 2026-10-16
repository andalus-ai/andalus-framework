package tech.kayys.andalus.execution.turn;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record AgentTurn(
        TurnId id,
        String executionId,
        long sequence,
        TurnContext context,
        TurnInput input,
        List<TurnObservation> observations,
        Object decision,
        Object outcome,
        Instant startedAt,
        Instant completedAt,
        Map<String, Object> metadata
) {

    public AgentTurn {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(context, "context cannot be null");
        Objects.requireNonNull(input, "input cannot be null");
        Objects.requireNonNull(startedAt, "startedAt cannot be null");

        observations = observations == null
                ? List.of()
                : List.copyOf(observations);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean completed() {
        return completedAt != null;
    }
}
