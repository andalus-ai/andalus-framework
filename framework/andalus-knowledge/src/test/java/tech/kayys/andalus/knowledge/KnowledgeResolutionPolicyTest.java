package tech.kayys.andalus.knowledge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KnowledgeResolutionPolicyTest {
    @Test void allowAllPolicyAllows() throws Exception {
        var policy = new AllowAllKnowledgeResolutionPolicy();
        var query = KnowledgeQuery.of("test");
        var ctx = KnowledgeContext.empty();
        var request = KnowledgeResolutionRequest.of(query, ctx);
        var source = mockSource("s1");
        var decision = policy.evaluate(request, source).toCompletableFuture().get();
        assertTrue(decision.allowed());
    }
    @Test void denyDecisionCarriesReason() {
        var d = KnowledgeResolutionDecision.deny("tenant mismatch");
        assertFalse(d.allowed());
        assertEquals("tenant mismatch", d.reason());
    }
    @Test void denyWithNullReasonGetsDefault() {
        var d = KnowledgeResolutionDecision.deny(null);
        assertFalse(d.allowed());
        assertFalse(d.reason().isBlank());
    }
    @Test void sourceSelectionResultDefaults() {
        var result = KnowledgeSourceSelectionResult.empty();
        assertTrue(result.selected().isEmpty());
        assertTrue(result.excluded().isEmpty());
    }
    @Test void sourceFailureCodeConstants() {
        assertEquals("NOT_FOUND", KnowledgeSourceFailure.CODE_NOT_FOUND);
        assertEquals("QUERY_FAILED", KnowledgeSourceFailure.CODE_QUERY_FAILED);
    }

    private KnowledgeSource mockSource(String id) {
        return new KnowledgeSource() {
            @Override public String id() { return id; }
            @Override public java.util.concurrent.CompletionStage<KnowledgeResult> query(
                    KnowledgeQuery q, KnowledgeContext c) {
                return java.util.concurrent.CompletableFuture.completedFuture(KnowledgeResult.empty(q));
            }
        };
    }
}
