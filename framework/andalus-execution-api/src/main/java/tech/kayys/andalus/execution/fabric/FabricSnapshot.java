package tech.kayys.andalus.execution.fabric;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Point-in-time snapshot of the distributed execution fabric.
 */
public record FabricSnapshot(
    String fabricId,
    Instant timestamp,
    List<AndalusNode> nodes,
    Map<String, Object> attributes
) {
    public FabricSnapshot {
        Objects.requireNonNull(fabricId, "fabricId cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");
        nodes = nodes != null ? List.copyOf(nodes) : List.of();
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }

    public List<AndalusNode> availableNodes() {
        return nodes.stream()
            .filter(n -> n.status().isAvailable())
            .toList();
    }
}
