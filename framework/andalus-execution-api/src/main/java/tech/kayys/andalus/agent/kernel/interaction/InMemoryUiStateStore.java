
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryUiStateStore
        implements UiStateStore {

    private final Map<String, UiState> states =
            new ConcurrentHashMap<>();

    @Override
    public UiState create(
            String runId,
            String surfaceId
    ) {
        UiState state =
                new UiState(
                        surfaceId,
                        runId,
                        Map.of(),
                        0
                );

        states.put(
                key(runId, surfaceId),
                state
        );

        return state;
    }

    @Override
    public Optional<UiState> find(
            String runId,
            String surfaceId
    ) {
        return Optional.ofNullable(
                states.get(
                        key(runId, surfaceId)
                )
        );
    }

    @Override
    public UiState update(
            UiState state
    ) {
        states.put(
                key(
                        state.runId(),
                        state.surfaceId()
                ),
                state
        );

        return state;
    }

    @Override
    public void remove(
            String runId,
            String surfaceId
    ) {
        states.remove(
                key(runId, surfaceId)
        );
    }

    private String key(
            String runId,
            String surfaceId
    ) {
        return runId + "::" + surfaceId;
    }
}
