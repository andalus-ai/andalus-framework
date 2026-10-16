package tech.kayys.andalus.harness.memory;

/**
 * Semantic lifecycle states of a memory item throughout its retention timeline.
 */
public enum MemoryLifecycleState {
    CANDIDATE,
    EVALUATED,
    REJECTED,
    ADMITTED,
    ACTIVE,
    EXPIRED,
    ARCHIVED,
    DELETED
}
