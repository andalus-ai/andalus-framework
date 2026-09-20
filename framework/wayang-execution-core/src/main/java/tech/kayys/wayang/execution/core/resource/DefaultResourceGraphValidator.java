package tech.kayys.wayang.execution.core.resource;

import tech.kayys.wayang.execution.resource.graph.ResourceEdge;
import tech.kayys.wayang.execution.resource.graph.ResourceGraph;
import tech.kayys.wayang.execution.resource.graph.ResourceGraphValidator;
import tech.kayys.wayang.execution.resource.model.ResourceId;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Standard implementation of {@link ResourceGraphValidator} validating node existence and acyclicity.
 */
public class DefaultResourceGraphValidator implements ResourceGraphValidator {

    @Override
    public boolean validate(ResourceGraph graph) {
        if (graph == null) {
            return false;
        }

        Set<ResourceId> knownIds = new HashSet<>();
        graph.resources().forEach(r -> knownIds.add(r.id()));

        // 1. Validate edge endpoints
        for (ResourceEdge edge : graph.edges()) {
            if (!knownIds.contains(edge.source()) || !knownIds.contains(edge.target())) {
                return false;
            }
            if (edge.source().equals(edge.target())) {
                return false; // self-loop
            }
        }

        // 2. Check for cycles via DFS
        Map<ResourceId, Set<ResourceId>> adjacency = new HashMap<>();
        for (ResourceEdge edge : graph.edges()) {
            adjacency.computeIfAbsent(edge.source(), k -> new HashSet<>()).add(edge.target());
        }

        Set<ResourceId> visited = new HashSet<>();
        Set<ResourceId> recStack = new HashSet<>();

        for (ResourceId id : knownIds) {
            if (!visited.contains(id)) {
                if (hasCycle(id, adjacency, visited, recStack)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasCycle(ResourceId current, Map<ResourceId, Set<ResourceId>> adj,
                             Set<ResourceId> visited, Set<ResourceId> recStack) {
        visited.add(current);
        recStack.add(current);

        Set<ResourceId> neighbors = adj.get(current);
        if (neighbors != null) {
            for (ResourceId neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    if (hasCycle(neighbor, adj, visited, recStack)) {
                        return true;
                    }
                } else if (recStack.contains(neighbor)) {
                    return true;
                }
            }
        }

        recStack.remove(current);
        return false;
    }
}
