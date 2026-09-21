package tech.kayys.andalus.memory.session;

/**
 * Architectural memory tiers for hierarchical agent session memory.
 */
public enum MemoryTier {
    /**
     * L0: Active context window (immediate prompt/reasoning tokens).
     */
    L0_ACTIVE_CONTEXT,

    /**
     * L1: Working session memory (recent observations, scratchpad, ephemeral graph).
     */
    L1_WORKING_SESSION,

    /**
     * L2: Durable session memory (decisions, state checkpoints, validated facts).
     */
    L2_DURABLE_SESSION,

    /**
     * L3: External knowledge (repositories, vector stores, long-term indexes).
     */
    L3_EXTERNAL_KNOWLEDGE
}
