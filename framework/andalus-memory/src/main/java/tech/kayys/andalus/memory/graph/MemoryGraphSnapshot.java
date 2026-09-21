package tech.kayys.andalus.memory.graph;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Immutable snapshot of the entire working memory graph across worker handoffs.
 */
public record MemoryGraphSnapshot(
    String snapshotId,
    List<MemoryNode> nodes,
    List<MemoryEdge> edges,
    long revision,
    Instant createdAt
) implements Serializable {

    public MemoryGraphSnapshot {
        Objects.requireNonNull(snapshotId, "snapshotId cannot be null");
        Objects.requireNonNull(createdAt, "createdAt cannot be null");
        nodes = nodes != null ? List.copyOf(nodes) : List.of();
        edges = edges != null ? List.copyOf(edges) : List.of();
    }

    public static MemoryGraphSnapshot of(String snapshotId, List<MemoryNode> nodes, List<MemoryEdge> edges, long revision) {
        return new MemoryGraphSnapshot(snapshotId, nodes, edges, revision, Instant.now());
    }

    public static MemoryGraphSnapshot empty(String snapshotId) {
        return new MemoryGraphSnapshot(snapshotId, List.of(), List.of(), 0L, Instant.now());
    }
}
