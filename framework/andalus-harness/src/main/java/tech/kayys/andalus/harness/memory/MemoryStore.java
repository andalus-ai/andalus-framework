package tech.kayys.andalus.harness.memory;

import java.util.Optional;

/**
 * Durability and retrieval contract for long-lived agent and project memory.
 */
public interface MemoryStore {

    MemoryId store(MemoryEntry entry);

    Optional<MemoryEntry> get(MemoryId id);

    void delete(MemoryId id);

    MemoryQueryResult query(MemoryQuery query);
}
