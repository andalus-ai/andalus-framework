package tech.kayys.andalus.spi.agent;

/**
 * Protocols through which an agent may be invoked or integrated.
 *
 * <p>These are integration capabilities, not requirements that every
 * Andalus deployment must implement.</p>
 */
public enum AgentProtocol {

    IN_PROCESS,

    GRPC,

    HTTP,

    WEBSOCKET,

    MESSAGE_BUS,

    A2A,

    MCP
}
