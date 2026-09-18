package tech.kayys.wayang.execution.governance.limits;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

public record LimitDefinition(
        String id,
        LimitType type,
        long capacity,
        Duration window,
        String scope,
        Map<String, String> dimensions,
        Map<String, Object> attributes
) {

    public LimitDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "id cannot be null or blank"
            );
        }

        id = id.trim();

        Objects.requireNonNull(
                type,
                "type cannot be null"
        );

        if (capacity < 0) {
            throw new IllegalArgumentException(
                    "capacity cannot be negative"
            );
        }

        if (window != null &&
                (window.isNegative() || window.isZero())) {

            throw new IllegalArgumentException(
                    "window must be positive"
            );
        }

        scope = scope == null || scope.isBlank()
                ? "tenant"
                : scope.trim();

        dimensions = dimensions == null
                ? Map.of()
                : Map.copyOf(dimensions);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
