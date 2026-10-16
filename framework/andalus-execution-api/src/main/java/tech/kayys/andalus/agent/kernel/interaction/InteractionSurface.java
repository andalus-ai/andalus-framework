
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Map;

public final class InteractionSurface {

    private final String runId;
    private final String surfaceId;
    private final PresentationRouter router;
    private final UiStateController state;

    public InteractionSurface(
            String runId,
            String surfaceId,
            PresentationRouter router,
            UiStateController state
    ) {
        this.runId = runId;
        this.surfaceId = surfaceId;
        this.router = router;
        this.state = state;
    }

    public String id() {
        return surfaceId;
    }

    public UiState state() {
        return state.state(
                runId,
                surfaceId
        );
    }

    public UiState update(
            Map<String, Object> values
    ) {
        return state.update(
                runId,
                surfaceId,
                values
        );
    }

    public void present(
            Presentation presentation
    ) {
        PresentationEnvelope envelope =
                new PresentationEnvelope(
                        java.util.UUID.randomUUID().toString(),
                        runId,
                        PresentationTarget.main(
                                surfaceId
                        ),
                        presentation,
                        null,
                        Map.of()
                );

        router.publish(envelope);
    }
}
