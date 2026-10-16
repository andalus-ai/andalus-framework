
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Map;

public final class InteractionPresentationRouter
        implements PresentationRouter {

    private final InteractionBus bus;

    public InteractionPresentationRouter(
            InteractionBus bus
    ) {
        this.bus = bus;
    }

    @Override
    public void publish(
            PresentationEnvelope envelope
    ) {
        bus.publish(
                envelope.runId(),
                InteractionEventType.PRESENTATION,
                Map.of(
                        "envelope",
                        envelope
                )
        );
    }
}
