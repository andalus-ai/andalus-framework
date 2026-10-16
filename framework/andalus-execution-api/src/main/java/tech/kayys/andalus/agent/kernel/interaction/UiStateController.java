
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Map;

public final class UiStateController {

    private final UiStateStore store;
    private final InteractionBus bus;

    public UiStateController(
            UiStateStore store,
            InteractionBus bus
    ) {
        this.store = store;
        this.bus = bus;
    }

    public UiState createSurface(
            String runId,
            String surfaceId
    ) {
        UiState state =
                store.create(
                        runId,
                        surfaceId
                );

        bus.publish(
                runId,
                InteractionEventType.UI_STATE_CHANGED,
                Map.of(
                        "surfaceId",
                        surfaceId,
                        "state",
                        state
                )
        );

        return state;
    }

    public UiState update(
            String runId,
            String surfaceId,
            Map<String, Object> values
    ) {
        UiState current =
                store.find(
                        runId,
                        surfaceId
                ).orElseGet(() ->
                        store.create(
                                runId,
                                surfaceId
                        ));

        UiState next =
                current.with(values);

        store.update(next);

        bus.publish(
                runId,
                InteractionEventType.UI_STATE_CHANGED,
                Map.of(
                        "surfaceId",
                        surfaceId,
                        "state",
                        next
                )
        );

        return next;
    }

    public UiState state(
            String runId,
            String surfaceId
    ) {
        return store.find(
                runId,
                surfaceId
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Unknown UI surface: "
                                + surfaceId
                ));
    }

    public void remove(
            String runId,
            String surfaceId
    ) {
        store.remove(
                runId,
                surfaceId
        );

        bus.publish(
                runId,
                InteractionEventType.UI_STATE_CHANGED,
                Map.of(
                        "surfaceId",
                        surfaceId,
                        "removed",
                        true
                )
        );
    }
}
