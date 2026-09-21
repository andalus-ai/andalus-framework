package tech.kayys.andalus.memory.graph;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * An individual node within an agent's working memory graph.
 */
public record MemoryNode(
    String nodeId,
    MemoryNodeType type,
    String content,
    double confidence,
    Instant timestamp,
    Set<String> tags
) implements Serializable {

    public MemoryNode {
        Objects.requireNonNull(nodeId, "nodeId cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(content, "content cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");
        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("confidence must be between 0.0 and 1.0");
        }
        tags = tags != null ? Set.copyOf(tags) : Set.of();
    }

    public static MemoryNode of(String nodeId, MemoryNodeType type, String content, double confidence) {
        return new MemoryNode(nodeId, type, content, confidence, Instant.now(), Set.of());
    }

    public static MemoryNode of(String nodeId, MemoryNodeType type, String content, double confidence, Set<String> tags) {
        return new MemoryNode(nodeId, type, content, confidence, Instant.now(), tags);
    }
}
