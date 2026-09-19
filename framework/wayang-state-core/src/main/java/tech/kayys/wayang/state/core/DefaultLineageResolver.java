package tech.kayys.wayang.state.core;

import tech.kayys.wayang.state.artifact.ArtifactId;
import tech.kayys.wayang.state.lineage.*;
import tech.kayys.wayang.state.provenance.ProvenanceRecord;
import tech.kayys.wayang.state.provenance.ProvenanceStore;
import tech.kayys.wayang.state.provenance.ProvenanceSubject;

import java.util.*;

public class DefaultLineageResolver implements LineageResolver {

    private final ProvenanceStore provenanceStore;

    public DefaultLineageResolver(ProvenanceStore provenanceStore) {
        this.provenanceStore = Objects.requireNonNull(provenanceStore, "provenanceStore cannot be null");
    }

    @Override
    public LineageGraph resolve(ArtifactId artifactId) {
        Map<String, LineageNode> nodes = new LinkedHashMap<>();
        List<LineageEdge> edges = new ArrayList<>();

        Queue<String> toTrace = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        toTrace.add(artifactId.value());

        while (!toTrace.isEmpty()) {
            String current = toTrace.poll();
            if (!visited.add(current)) {
                continue;
            }

            nodes.putIfAbsent(current, new LineageNode(current, "artifact", "Artifact: " + current, Map.of()));
            List<ProvenanceRecord> records = provenanceStore.findBySubject(ProvenanceSubject.artifact(current));

            for (ProvenanceRecord record : records) {
                String opNodeId = "op-" + record.id().value();
                nodes.putIfAbsent(opNodeId, new LineageNode(opNodeId, "operation", record.operation().name(), record.operation().attributes()));
                edges.add(new LineageEdge(opNodeId, current, "PRODUCED", Map.of()));

                for (var input : record.inputs()) {
                    String inId = input.reference().id().value();
                    nodes.putIfAbsent(inId, new LineageNode(inId, "artifact", input.name(), Map.of()));
                    edges.add(new LineageEdge(inId, opNodeId, "CONSUMED_BY", Map.of()));
                    toTrace.add(inId);
                }
            }
        }

        return new LineageGraph(new ArrayList<>(nodes.values()), edges);
    }

    @Override
    public LineageGraph resolveExecution(String executionId) {
        return new LineageGraph(
                List.of(new LineageNode(executionId, "execution", "Execution " + executionId, Map.of())),
                List.of()
        );
    }
}
