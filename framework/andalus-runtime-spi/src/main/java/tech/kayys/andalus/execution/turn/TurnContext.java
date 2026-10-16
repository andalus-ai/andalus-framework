package tech.kayys.andalus.execution.turn;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record TurnContext(
        TurnId turnId,
        String executionId,
        String agentId,
        long sequence,
        Map<String, Object> attributes
) {

    public TurnContext {
        Objects.requireNonNull(turnId, "turnId cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(agentId, "agentId cannot be null");

        if (executionId.isBlank()) {
            throw new IllegalArgumentException("executionId cannot be blank");
        }

        if (agentId.isBlank()) {
            throw new IllegalArgumentException("agentId cannot be blank");
        }

        if (sequence < 0) {
            throw new IllegalArgumentException(
                    "sequence cannot be negative");
        }

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public Optional<Object> attribute(String key) {
        return Optional.ofNullable(attributes.get(key));
    }

    public <T> Optional<T> attribute(
            String key,
            Class<T> type) {

        Object value = attributes.get(key);

        if (value != null && type.isInstance(value)) {
            return Optional.of(type.cast(value));
        }

        return Optional.empty();
    }
}
