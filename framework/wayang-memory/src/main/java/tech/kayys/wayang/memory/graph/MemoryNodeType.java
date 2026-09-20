package tech.kayys.wayang.memory.graph;

/**
 * Categorization of semantic memory graph nodes.
 */
public enum MemoryNodeType {
    /**
     * High-level objective or sub-goal of the agent.
     */
    GOAL,

    /**
     * Working hypothesis or assumption under evaluation.
     */
    HYPOTHESIS,

    /**
     * Concrete evidence extracted from tool execution, logs, or search.
     */
    EVIDENCE,

    /**
     * Verified ground truth derived from confirmed evidence.
     */
    FACT,

    /**
     * Explicit decision made by the agent's reasoning loop.
     */
    DECISION,

    /**
     * Raw observation from environment or user prompt.
     */
    OBSERVATION
}
