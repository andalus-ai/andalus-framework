
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Adapter between the protocol-neutral interaction layer
 * and an A2UI implementation.
 *
 * <p>The consumer is intentionally generic so the execution
 * module does not depend on a concrete A2UI transport.</p>
 */
public final class A2uiPresentationBridge
        implements InteractionSink {

    private final Consumer<PresentationEnvelope> sink;

    public A2uiPresentationBridge(
            Consumer<PresentationEnvelope> sink
    ) {
        this.sink =
                Objects.requireNonNull(sink);
    }

    @Override
    public void accept(
            InteractionEvent event
    ) {
        if (event.type()
                != InteractionEventType.PRESENTATION) {
            return;
        }

        Object value =
                event.data().get("envelope");

        if (!(value instanceof PresentationEnvelope envelope)) {
            return;
        }

        if (envelope.presentation().format()
                != PresentationFormat.A2UI) {
            return;
        }

        sink.accept(envelope);
    }
}
