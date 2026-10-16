
package tech.kayys.andalus.agent.kernel.interaction;

/**
 * Live subscription to AgentRun interaction events.
 */
public interface InteractionSubscription
        extends AutoCloseable {

    boolean active();

    @Override
    void close();
}
