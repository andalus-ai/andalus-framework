package tech.kayys.andalus.knowledge;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class KnowledgeRuntimeContextTest {
    @Test void factoryMethodSetsAgentAndExecution() {
        var ctx = KnowledgeRuntimeContext.of("agent-1", "exec-1");
        assertEquals("agent-1", ctx.agentId());
        assertEquals("exec-1", ctx.executionId());
        assertNotNull(ctx.asOf());
    }
    @Test void nullSourceIdsBecomesEmpty() {
        var ctx = new KnowledgeRuntimeContext(null, null, null, null, null, null, null, Instant.now(), null, null);
        assertEquals(List.of(), ctx.sourceIds());
    }
    @Test void invocationGeneratesId() {
        var inv = KnowledgeInvocation.forExecution("exec-1", "trace-1");
        assertNotNull(inv.invocationId());
        assertEquals("exec-1", inv.executionId());
        assertEquals("trace-1", inv.traceId());
    }
    @Test void budgetProviderDefaults() {
        var provider = KnowledgeBudgetProvider.defaults();
        var budget = provider.budgetFor(KnowledgeRuntimeContext.of("a", "e"));
        assertNotNull(budget);
        assertTrue(budget.maxItems() > 0);
    }
    @Test void budgetProviderFixed() {
        var fixed = KnowledgeBudget.strict();
        var provider = KnowledgeBudgetProvider.fixed(fixed);
        assertEquals(fixed, provider.budgetFor(KnowledgeRuntimeContext.of("a", "e")));
    }
    @Test void defaultContextFactoryMapsFields() {
        var factory = new DefaultKnowledgeContextFactory();
        var runtime = new KnowledgeRuntimeContext("t1", "u1", "a1", "s1", "e1", "ws1", "proj1",
                Instant.now(), List.of("src1"), java.util.Map.of());
        var ctx = factory.create(runtime);
        assertEquals("t1", ctx.tenantId());
        assertEquals("a1", ctx.agentId());
        assertEquals(List.of("src1"), ctx.sourceIds());
    }
}
