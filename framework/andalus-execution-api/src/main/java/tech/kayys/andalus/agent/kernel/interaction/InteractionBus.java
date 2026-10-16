
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.List;
import java.util.Map;

/**
 * Event stream for AgentRun interaction.
 *
 * <p>The bus provides both replay and live subscription,
 * which makes it suitable for streaming UIs and reconnecting
 * clients.</p>
 */
public interface InteractionBus {

    InteractionEvent publish(
            String runId,
            InteractionEventType type,
            Map<String, Object> data
    );

    InteractionSubscription subscribe(
            String runId,
            long afterSequence,
            InteractionSink sink
    );

    List<InteractionEvent> history(
            String runId,
            long afterSequence
    );
}
