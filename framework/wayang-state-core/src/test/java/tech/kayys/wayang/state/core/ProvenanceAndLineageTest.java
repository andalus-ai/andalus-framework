package tech.kayys.wayang.state.core;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.state.artifact.*;
import tech.kayys.wayang.state.lineage.LineageGraph;
import tech.kayys.wayang.state.provenance.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ProvenanceAndLineageTest {

    @Test
    void testProvenanceTrackingAndLineageResolution() {
        InMemoryArtifactStore artifactStore = new InMemoryArtifactStore();
        InMemoryProvenanceStore provenanceStore = new InMemoryProvenanceStore();
        DefaultLineageResolver lineageResolver = new DefaultLineageResolver(provenanceStore);

        // 1. Create source artifact
        ArtifactReference source = artifactStore.put(ArtifactInput.of("source.py", ArtifactType.FILE, "print(1)".getBytes(StandardCharsets.UTF_8)));

        // 2. Create transformed artifact
        ArtifactReference transformed = artifactStore.put(ArtifactInput.of("transformed.py", ArtifactType.FILE, "print(1 + 1)".getBytes(StandardCharsets.UTF_8)));

        // 3. Record provenance
        ProvenanceRecord record = new ProvenanceRecord(
                ProvenanceId.random(),
                ProvenanceSubject.artifact(transformed.id().value()),
                ProvenanceOperation.of("patch-file", "transformation"),
                List.of(ProvenanceInput.of("original", source)),
                List.of(transformed),
                ProvenanceActor.of(ProvenanceActorType.AGENT, "coding-agent"),
                ProvenanceEnvironment.of("sandbox-1", "worker-1"),
                Instant.now(),
                Map.of()
        );
        provenanceStore.append(record);

        // Query provenance
        List<ProvenanceRecord> forTransformed = provenanceStore.findBySubject(ProvenanceSubject.artifact(transformed.id().value()));
        assertEquals(1, forTransformed.size());
        assertEquals("coding-agent", forTransformed.get(0).actor().id());

        // Resolve Lineage
        LineageGraph graph = lineageResolver.resolve(transformed.id());
        assertFalse(graph.nodes().isEmpty());
        assertFalse(graph.edges().isEmpty());

        assertTrue(graph.nodes().stream().anyMatch(n -> n.id().equals(transformed.id().value())));
        assertTrue(graph.nodes().stream().anyMatch(n -> n.id().equals(source.id().value())));
    }
}
