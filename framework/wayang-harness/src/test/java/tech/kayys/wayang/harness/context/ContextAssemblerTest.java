package tech.kayys.wayang.harness.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContextAssemblerTest {

    private DefaultContextAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new DefaultContextAssembler(List.of());
    }

    @Test
    void testPrioritySortingAndAssembly() {
        assembler.register(req -> ContextContribution.of("p1", List.of(
                ContextItem.of(ContextKind.CONVERSATION, "hello user", ContextPriority.LOW),
                ContextItem.of(ContextKind.SYSTEM, "strict system prompt", ContextPriority.CRITICAL)
        )));

        assembler.register(req -> ContextContribution.of("p2", List.of(
                ContextItem.of(ContextKind.MEMORY, "project uses Java 25", ContextPriority.HIGH)
        )));

        AssembledContext assembled = assembler.assemble(ContextRequest.of("test", 1000L));
        assertNotNull(assembled);
        assertEquals(3, assembled.items().size());

        // Verify priorities order: CRITICAL -> HIGH -> LOW
        assertEquals(ContextPriority.CRITICAL, assembled.items().get(0).priority());
        assertEquals(ContextPriority.HIGH, assembled.items().get(1).priority());
        assertEquals(ContextPriority.LOW, assembled.items().get(2).priority());
    }

    @Test
    void testTokenBudgetTruncation() {
        // String of length 200 chars ~ 50 tokens
        String largeContent = "a".repeat(200);

        assembler.register(req -> ContextContribution.of("p1", List.of(
                ContextItem.of(ContextKind.SYSTEM, "sys", ContextPriority.CRITICAL),
                ContextItem.of(ContextKind.OBSERVATION, largeContent, ContextPriority.HIGH),
                ContextItem.of(ContextKind.CONVERSATION, largeContent, ContextPriority.LOW)
        )));

        // Set maxTokens to 60 (can fit CRITICAL (1 token) + HIGH (50 tokens), but not LOW)
        AssembledContext assembled = assembler.assemble(ContextRequest.of("test", 60L));
        assertEquals(2, assembled.items().size());
        assertEquals(ContextPriority.CRITICAL, assembled.items().get(0).priority());
        assertEquals(ContextPriority.HIGH, assembled.items().get(1).priority());
    }
}
