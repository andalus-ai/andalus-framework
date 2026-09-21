package tech.kayys.andalus.memory.session;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InMemorySessionMemoryTest {

    @Test
    void testRetainResolveAndForget() {
        InMemorySessionMemory memory = new InMemorySessionMemory();
        MemoryEntry entry = MemoryEntry.of(
                "entry-1",
                MemoryTier.L2_DURABLE_SESSION,
                "Architecture decision: Decouple Session from Execution",
                Map.of("category", "decision", "version", "v4.47"));

        MemoryReference ref = memory.retain(entry);
        assertNotNull(ref);
        assertEquals(MemoryTier.L2_DURABLE_SESSION, ref.tier());

        Optional<MemoryEntry> resolved = memory.resolve(ref);
        assertTrue(resolved.isPresent());
        assertEquals("entry-1", resolved.get().entryId());
        assertEquals("Architecture decision: Decouple Session from Execution", resolved.get().content());

        memory.forget(ref);
        Optional<MemoryEntry> afterForget = memory.resolve(ref);
        assertTrue(afterForget.isEmpty());
    }
}
