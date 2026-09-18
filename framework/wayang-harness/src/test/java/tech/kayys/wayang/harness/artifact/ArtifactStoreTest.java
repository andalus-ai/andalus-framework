package tech.kayys.wayang.harness.artifact;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ArtifactStoreTest {

    @Test
    void testPutRetrieveAndDigest() {
        InMemoryArtifactStore store = new InMemoryArtifactStore();
        ExecutionId execId = ExecutionId.of("exec-test");

        ArtifactContent content = ArtifactContent.ofText("System patch content for test");
        ArtifactMetadata metadata = ArtifactMetadata.text("patch-01.diff", content.size());
        ArtifactProvenance provenance = ArtifactProvenance.ofExecution(execId);

        Artifact artifact = store.put(content, metadata, provenance);
        assertNotNull(artifact.id());
        assertNotNull(artifact.digest());
        assertEquals("SHA-256", artifact.digest().algorithm());

        Optional<ArtifactContent> retrieved = store.get(artifact.id());
        assertTrue(retrieved.isPresent());
        assertEquals("System patch content for test", retrieved.get().asText());

        Optional<Artifact> desc = store.describe(artifact.id());
        assertTrue(desc.isPresent());
        assertEquals("patch-01.diff", desc.get().descriptor().name());
    }

    @Test
    void testRetentionAndRelease() {
        InMemoryArtifactStore store = new InMemoryArtifactStore();
        ExecutionId execId = ExecutionId.of("exec-test");

        ArtifactContent content = ArtifactContent.ofText("Temporary file");
        Artifact artifact = store.put(content, ArtifactMetadata.text("temp.txt", content.size()), ArtifactProvenance.ofExecution(execId));

        store.release(artifact.id());
        assertTrue(store.get(artifact.id()).isEmpty());

        // Retained artifact
        Artifact kept = store.put(content, ArtifactMetadata.text("keep.txt", content.size()), ArtifactProvenance.ofExecution(execId));
        store.retain(kept.id());
        store.release(kept.id()); // Should not delete because retained
        assertTrue(store.get(kept.id()).isPresent());
    }
}
