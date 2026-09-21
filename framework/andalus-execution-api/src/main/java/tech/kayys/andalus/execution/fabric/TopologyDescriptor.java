package tech.kayys.andalus.execution.fabric;

import java.util.Map;
import java.util.Objects;

/**
 * Topographical location and affinity labels of a node.
 */
public record TopologyDescriptor(
    String region,
    String zone,
    String cluster,
    Map<String, String> labels
) {
    public TopologyDescriptor {
        Objects.requireNonNull(region, "region cannot be null");
        Objects.requireNonNull(zone, "zone cannot be null");
        Objects.requireNonNull(cluster, "cluster cannot be null");
        labels = labels != null ? Map.copyOf(labels) : Map.of();
    }

    public static TopologyDescriptor local() {
        return new TopologyDescriptor("local", "local-zone", "local-cluster", Map.of("environment", "local"));
    }

    public static TopologyDescriptor of(String region, String zone, String cluster) {
        return new TopologyDescriptor(region, zone, cluster, Map.of());
    }
}
