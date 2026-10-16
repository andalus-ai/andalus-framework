
package tech.kayys.andalus.agent.kernel.interaction;

public final class InteractionSurfaceFactory {

    private final PresentationRouter router;
    private final UiStateController state;

    public InteractionSurfaceFactory(
            PresentationRouter router,
            UiStateController state
    ) {
        this.router = router;
        this.state = state;
    }

    public InteractionSurface open(
            String runId,
            String surfaceId
    ) {
        state.createSurface(
                runId,
                surfaceId
        );

        return new InteractionSurface(
                runId,
                surfaceId,
                router,
                state
        );
    }
}
