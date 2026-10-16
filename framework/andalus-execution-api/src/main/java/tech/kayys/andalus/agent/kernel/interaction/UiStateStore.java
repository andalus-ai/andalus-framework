
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Optional;

public interface UiStateStore {

    UiState create(
            String runId,
            String surfaceId
    );

    Optional<UiState> find(
            String runId,
            String surfaceId
    );

    UiState update(
            UiState state
    );

    void remove(
            String runId,
            String surfaceId
    );
}
