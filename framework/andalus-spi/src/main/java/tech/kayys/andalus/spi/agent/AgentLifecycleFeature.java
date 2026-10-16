package tech.kayys.andalus.spi.agent;

/**
 * Lifecycle capabilities advertised by an agent.
 */
public enum AgentLifecycleFeature {

    INITIALIZE,

    EXECUTE,

    PAUSE,

    RESUME,

    CANCEL,

    CHECKPOINT,

    RECOVER,

    DRAIN,

    STREAM,

    LONG_RUNNING
}
