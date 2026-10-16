package tech.kayys.andalus.knowledge;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class KnowledgeDefinitionTest {
    @Test void blankIdThrows() {
        var ref = KnowledgeSourceReference.of("src", "type");
        assertThrows(IllegalArgumentException.class,
                () -> new KnowledgeDefinition(" ", "1.0", null, null, ref, null, null, null));
    }
    @Test void blankVersionThrows() {
        var ref = KnowledgeSourceReference.of("src", "type");
        assertThrows(IllegalArgumentException.class,
                () -> new KnowledgeDefinition("id", " ", null, null, ref, null, null, null));
    }
    @Test void nullCapabilitiesBecomesEmpty() {
        var def = new KnowledgeDefinition("id", "1.0", null, null,
                KnowledgeSourceReference.of("src", "type"), null, null, null);
        assertEquals(List.of(), def.capabilities());
    }
    @Test void capabilitiesAreImmutable() {
        var def = KnowledgeDefinition.of("id", "1.0", KnowledgeSourceReference.of("src", "http"));
        assertThrows(UnsupportedOperationException.class, () -> def.capabilities().add("x"));
    }
    @Test void nameDefaultsToId() {
        var def = new KnowledgeDefinition("my-id", "1.0", null, null,
                KnowledgeSourceReference.of("src", "type"), null, null, null);
        assertEquals("my-id", def.name());
    }
    @Test void sourceReferenceBlankIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> KnowledgeSourceReference.of(" ", "type"));
    }
}
