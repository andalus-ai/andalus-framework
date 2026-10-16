package tech.kayys.andalus.knowledge.context;

import tech.kayys.andalus.knowledge.KnowledgeBudget;
import tech.kayys.andalus.knowledge.KnowledgeInvocation;
import tech.kayys.andalus.knowledge.KnowledgeQuery;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RetrievalContextTest {

    @Test
    void nullQueryTextThrows() {
        assertThrows(NullPointerException.class, () -> RetrievalContext.of(null));
    }

    @Test
    void defaultValuesInitialized() {
        var ctx = RetrievalContext.of("test query");
        assertEquals("test query", ctx.queryText());
        assertEquals("default", ctx.scope());
        assertNotNull(ctx.asOf());
        assertNotNull(ctx.budget());
        assertTrue(ctx.sourceConstraints().isEmpty());
        assertTrue(ctx.filters().isEmpty());
        assertTrue(ctx.invocation().isEmpty());
    }

    @Test
    void withModifiersCreateNewInstances() {
        var ctx = RetrievalContext.of("query")
                .withScope("finance")
                .withBudget(KnowledgeBudget.strict())
                .withSources(List.of("src-1", "src-2"))
                .withInvocation(new KnowledgeInvocation("inv-1", "exec-1", "turn-1", "trace-1"));

        assertEquals("finance", ctx.scope());
        assertEquals(KnowledgeBudget.strict(), ctx.budget());
        assertEquals(List.of("src-1", "src-2"), ctx.sourceConstraints());
        assertTrue(ctx.invocation().isPresent());
        assertEquals("exec-1", ctx.invocation().get().executionId());
    }

    @Test
    void convertsToKnowledgeQuery() {
        var budget = new KnowledgeBudget(10, 1024, 4096, 2, 2, false, false, 0.5);
        var ctx = RetrievalContext.of("search text")
                .withScope("custom-scope")
                .withBudget(budget);

        KnowledgeQuery query = ctx.toKnowledgeQuery();
        assertEquals("search text", query.text());
        assertEquals(10, query.topK());
        assertEquals(0.5, query.minScore(), 0.001);
        assertEquals("custom-scope", query.scope());
    }
}
