package tech.kayys.wayang.spi.operator;

import java.util.Map;
import java.util.Optional;

public record OperatorContext(
        String tenantId,
        String userId,
        String correlationId,
        String requestId,
        Map<String, Object> attributes
) {

    public OperatorContext {
        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public Optional<Object> attribute(String name) {
        return Optional.ofNullable(attributes.get(name));
    }
}
