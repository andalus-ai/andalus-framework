
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Map;
import java.util.Objects;

public record UiState(
        String surfaceId,
        String runId,
        Map<String, Object> values,
        long version
) {

    public UiState {
        Objects.requireNonNull(surfaceId, "surfaceId");
        Objects.requireNonNull(runId, "runId");

        values = values == null
                ? Map.of()
                : Map.copyOf(values);

        if (version < 0) {
            throw new IllegalArgumentException(
                    "version must be >= 0"
            );
        }
    }

    public UiState with(
            Map<String, Object> newValues
    ) {
        return new UiState(
                surfaceId,
                runId,
                newValues,
                version + 1
        );
    }
}
