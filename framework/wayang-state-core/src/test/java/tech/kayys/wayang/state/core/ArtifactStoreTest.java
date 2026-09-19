package tech.kayys.wayang.state.core;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.state.artifact.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ArtifactStoreTest {

    @Test
    void testContentAddressingAndDeduplication() {
        InMemoryArtifactStore store = new InMemoryArtifactStore();

        byte[] content = "public class Foo {}".getBytes(StandardCharsets.UTF_8);
        ArtifactInput input1 = ArtifactInput.of("Foo.java", ArtifactType.FILE, content);

        ArtifactReference ref1 = store.put(input1);
        assertNotNull(ref1);
        assertNotNull(ref1.id());
        assertEquals("SHA-256", ref1.digest().algorithm());

        // Put identical content under different name
        ArtifactInput input2 = ArtifactInput.of("FooClone.java", ArtifactType.FILE, content);
        ArtifactReference ref2 = store.put(input2);

        // Deduplication must return identical reference with same ID and digest
        assertEquals(ref1.id(), ref2.id());
        assertEquals(ref1.digest(), ref2.digest());

        // Retrieval
        Optional<ArtifactContent> retrieved = store.get(ref1);
        assertTrue(retrieved.isPresent());
        assertArrayEquals(content, retrieved.get().data());

        // Find by digest
        assertTrue(store.exists(ref1.digest()));
        Optional<ArtifactReference> byDigest = store.findByDigest(ref1.digest());
        assertTrue(byDigest.isPresent());
        assertEquals(ref1.id(), byDigest.get().id());
    }
}
