
package tech.kayys.andalus.agent.kernel.interaction;

/**
 * Receives ordered Agent interaction events.
 */
@FunctionalInterface
public interface InteractionSink {

    void accept(
            InteractionEvent event
    );
}
