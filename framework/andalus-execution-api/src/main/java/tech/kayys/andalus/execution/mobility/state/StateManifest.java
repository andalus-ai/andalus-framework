package tech.kayys.andalus.execution.mobility.state;

import java.util.List;
import java.util.Objects;

/**
 * Immutable manifest of all state resources comprising an execution boundary.
 */
public record StateManifest(
        String executionId,
        long versionSequence,
        List<StateEntry> entries,
        String digest
) {
    public StateManifest {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        entries = entries == null ? List.of() : List.copyOf(entries);
        Objects.requireNonNull(digest, "digest cannot be null");
    }
}
