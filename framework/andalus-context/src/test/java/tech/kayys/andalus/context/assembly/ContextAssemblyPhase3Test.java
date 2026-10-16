package tech.kayys.andalus.context.assembly;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.context.compaction.ContextSlice;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContextAssemblyPhase3Test {

    @Test
    void contextPriorityRanksCorrectly() {
        assertTrue(ContextPriority.CRITICAL.rank() > ContextPriority.HIGH.rank());
        assertTrue(ContextPriority.HIGH.rank() > ContextPriority.NORMAL.rank());
        assertTrue(ContextPriority.NORMAL.rank() > ContextPriority.LOW.rank());
        assertTrue(ContextPriority.LOW.rank() > ContextPriority.OPTIONAL.rank());
    }

    @Test
    void textSerializerFormatsEnvelopeContent() {
        var slice1 = new ContextSlice(
                "slice-1",
                "You are a helpful economic analyst.",
                List.of("User: What is GDP?", "Assistant: GDP is gross domestic product."),
                List.of("Discussion focused on macroeconomic indicators"),
                120L,
                4000L
        );

        var envelope = new ContextEnvelope(
                "session-1",
                List.of(slice1),
                ContextBudget.standard(),
                120L,
                Instant.now()
        );

        var serializer = new DefaultTextContextSerializer();
        String serialized = serializer.serialize(envelope);

        assertNotNull(serialized);
        assertTrue(serialized.contains("=== SYSTEM INSTRUCTIONS ==="));
        assertTrue(serialized.contains("You are a helpful economic analyst."));
        assertTrue(serialized.contains("=== CONTEXT SUMMARY ==="));
        assertTrue(serialized.contains("Discussion focused on macroeconomic indicators"));
        assertTrue(serialized.contains("=== RECENT TURNS ==="));
        assertTrue(serialized.contains("User: What is GDP?"));
    }

    @Test
    void contextAssemblyDiagnosticsTracking() {
        var diag = ContextAssemblyDiagnostics.of(10, 6, 2400L, 45L);
        assertEquals(10, diag.candidatesCount());
        assertEquals(6, diag.selectedCount());
        assertEquals(2400L, diag.totalEstimatedTokens());
        assertEquals(45L, diag.durationMs());
    }
}
