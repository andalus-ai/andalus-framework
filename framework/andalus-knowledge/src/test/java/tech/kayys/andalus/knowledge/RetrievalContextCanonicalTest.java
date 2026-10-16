package tech.kayys.andalus.knowledge;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RetrievalContextCanonicalTest {

    @Test
    void ofCreatesRetrievalContextWithDefaults() {
        var query = KnowledgeQuery.of("economic trends");
        var ctx = new KnowledgeContext("t1", "u1", "a1", "s1", "domain", "scope", Instant.now(), List.of("s1"), Map.of());

        var retrievalCtx = RetrievalContext.of(query, ctx);
        assertEquals(query, retrievalCtx.query());
        assertEquals(ctx, retrievalCtx.knowledgeContext());
        assertEquals(List.of("s1"), retrievalCtx.sourceIds());
        assertNotNull(retrievalCtx.budget());
    }

    @Test
    void defaultFactoryAssemblesContext() {
        var factory = new DefaultRetrievalContextFactory();
        var query = KnowledgeQuery.of("inflation rate");
        var ctx = new KnowledgeContext("t1", "u1", "a1", "s1", "domain", "scope", Instant.now(), List.of("source-1"), Map.of());

        var retrievalCtx = factory.create(query, ctx);
        assertNotNull(retrievalCtx);
        assertEquals("inflation rate", retrievalCtx.query().text());
        assertEquals(List.of("source-1"), retrievalCtx.sourceIds());
    }
}
