package tech.kayys.wayang.state.lineage;

import java.util.List;
import java.util.Map;

public record LineageGraph(
        List<LineageNode> nodes,
        List<LineageEdge> edges
) {
    public LineageGraph {
        nodes = nodes == null ? List.of() : List.copyOf(nodes);
        edges = edges == null ? List.of() : List.copyOf(edges);
    }

    public static LineageGraph empty() {
        return new LineageGraph(List.of(), List.of());
    }
}
