package tech.kayys.andalus.memory.session;

/**
 * Semantic classification of session memory entries.
 * Preserves attribution and semantic intent across agent execution cycles.
 */
public enum MemoryKind {
    OBSERVATION,
    DECISION,
    FACT,
    PREFERENCE,
    TASK_STATE,
    SUMMARY,
    ARTIFACT_REFERENCE,
    EXTERNAL_KNOWLEDGE
}
