package tech.kayys.wayang.harness.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MemoryStoreTest {

    private InMemoryMemoryStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryMemoryStore();
    }

    @Test
    void testStoreAndGet() {
        MemoryEntry entry = MemoryEntry.fact("PostgreSQL 16 used in project", MemoryScope.PROJECT);
        MemoryId id = store.store(entry);

        Optional<MemoryEntry> loaded = store.get(id);
        assertTrue(loaded.isPresent());
        assertEquals("PostgreSQL 16 used in project", loaded.get().content());
        assertEquals(MemoryScope.PROJECT, loaded.get().scope());
    }

    @Test
    void testQueryByKeywordAndScope() {
        store.store(MemoryEntry.fact("Database is Postgres", MemoryScope.PROJECT));
        store.store(MemoryEntry.fact("User prefers dark mode", MemoryScope.AGENT));
        store.store(MemoryEntry.fact("Database replica in eu-west", MemoryScope.PROJECT));

        MemoryQueryResult result = store.query(MemoryQuery.of("database", MemoryScope.PROJECT));
        assertEquals(2, result.entries().size());

        MemoryQueryResult userPref = store.query(MemoryQuery.of("dark mode", MemoryScope.AGENT));
        assertEquals(1, userPref.entries().size());
    }

    @Test
    void testConflictResolver() {
        MemoryEntry candidate1 = new MemoryEntry(MemoryId.of("m1"), MemoryType.FACT, "Target JDK 21", MemoryScope.PROJECT,
                MemoryMetadata.observed("agent-1", "exec-1", 0.7));
        MemoryEntry candidate2 = new MemoryEntry(MemoryId.of("m2"), MemoryType.FACT, "Target JDK 25", MemoryScope.PROJECT,
                MemoryMetadata.observed("agent-2", "exec-2", 0.95));

        MemoryConflictResolver resolver = MemoryConflictResolver.highestConfidence();
        MemoryEntry resolved = resolver.resolve(List.of(candidate1, candidate2));

        assertNotNull(resolved);
        assertEquals("m2", resolved.id().value());
        assertEquals("Target JDK 25", resolved.content());
    }
}
