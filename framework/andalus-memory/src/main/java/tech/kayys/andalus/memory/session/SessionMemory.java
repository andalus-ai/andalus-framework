package tech.kayys.andalus.memory.session;

import java.util.Optional;

/**
 * Interface for governed session memory retention, retrieval, and eviction.
 * Andalus owns the contract and references; memory providers remain pluggable.
 */
public interface SessionMemory {

    /**
     * Retains a memory entry and returns a lightweight reference handle.
     */
    MemoryReference retain(MemoryEntry entry);

    /**
     * Resolves a memory entry by its reference handle.
     */
    Optional<MemoryEntry> resolve(MemoryReference reference);

    /**
     * Evicts/forgets a retained memory reference.
     */
    void forget(MemoryReference reference);
}
