package tech.kayys.andalus.execution.mobility.state;

/**
 * Declares durability semantics for execution state resources.
 */
public enum StateDurability {
    EPHEMERAL,
    CACHE,
    DURABLE
}
