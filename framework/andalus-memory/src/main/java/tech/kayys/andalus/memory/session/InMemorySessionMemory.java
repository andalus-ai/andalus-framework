package tech.kayys.andalus.memory.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe reference implementation of SessionMemory.
 */
public class InMemorySessionMemory implements SessionMemory {

    private final Map<String, MemoryEntry> entries = new ConcurrentHashMap<>();

    @Override
    public MemoryReference retain(MemoryEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("entry must not be null");
        }
        MemoryReference ref = MemoryReference.generate(entry.tier(), entry.entryId());
        entries.put(ref.referenceId(), entry);
        return ref;
    }

    @Override
    public Optional<MemoryEntry> resolve(MemoryReference reference) {
        if (reference == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entries.get(reference.referenceId()));
    }

    @Override
    public void forget(MemoryReference reference) {
        if (reference != null) {
            entries.remove(reference.referenceId());
        }
    }
}
