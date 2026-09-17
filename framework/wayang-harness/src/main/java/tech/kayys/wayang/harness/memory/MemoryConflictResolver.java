package tech.kayys.wayang.harness.memory;

import java.util.Comparator;
import java.util.List;

public interface MemoryConflictResolver {

    MemoryEntry resolve(List<MemoryEntry> candidates);

    static MemoryConflictResolver highestConfidence() {
        return candidates -> {
            if (candidates == null || candidates.isEmpty()) {
                return null;
            }
            return candidates.stream()
                    .max(Comparator.comparingDouble(c -> c.metadata().confidence()))
                    .orElse(candidates.get(0));
        };
    }
}
