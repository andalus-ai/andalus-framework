package tech.kayys.andalus.context.assembly;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.context.compaction.ContextSlice;

class DefaultContextAssemblerTest {

    @Test
    void testAssembleWithinBudget() {
        String sessionId = "sess-12345";
        ContextBudget budget = new ContextBudget(100L, 50L, 100L, 50L);
        ContextRequest request = new ContextRequest(sessionId, "test task", budget);

        ContextSlice slice1 = new ContextSlice("s1", "prompt context", List.of("turn 1"), List.of(), 80L, 200L);
        ContextSlice slice2 = new ContextSlice("s2", "supplemental context", List.of("turn 2"), List.of(), 100L, 200L);
        ContextSlice slice3 = new ContextSlice("s3", "overflow context", List.of("turn 3"), List.of(), 50L, 200L);

        DefaultContextAssembler assembler = new DefaultContextAssembler();
        ContextEnvelope envelope = assembler.assemble(request, List.of(slice1, slice2, slice3));

        assertNotNull(envelope);
        assertEquals(sessionId, envelope.sessionId());
        // budget total for input + retrieval = 200L.
        // slice1 (80) + slice2 (100) = 180 <= 200. slice3 (50) pushes to 230 > 200, so omitted.
        assertEquals(2, envelope.slices().size());
        assertEquals(180L, envelope.estimatedTokens());
        assertEquals("s1", envelope.slices().get(0).sliceId());
        assertEquals("s2", envelope.slices().get(1).sliceId());
    }
}
