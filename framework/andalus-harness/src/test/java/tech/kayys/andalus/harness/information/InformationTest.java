package tech.kayys.andalus.harness.information;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.information.evidence.Evidence;
import tech.kayys.andalus.harness.information.evidence.EvidenceId;
import tech.kayys.andalus.harness.information.knowledge.KnowledgeRetriever;
import tech.kayys.andalus.harness.information.knowledge.KnowledgeSourceId;
import tech.kayys.andalus.harness.information.knowledge.RetrievalResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InformationTest {

    @Test
    void testEvidenceProvenanceAndRetrieval() {
        Evidence evidence = Evidence.of("Andalus uses a neutral harness substrate.", "https://docs.andalus.tech/harness");
        assertNotNull(evidence.id());
        assertEquals("Andalus uses a neutral harness substrate.", evidence.text());
        assertEquals("https://docs.andalus.tech/harness", evidence.provenance().sourceUri());

        KnowledgeRetriever retriever = (sourceId, query, limit) -> new RetrievalResult(sourceId, query, List.of(evidence));

        KnowledgeSourceId sourceId = KnowledgeSourceId.of("wiki");
        RetrievalResult result = retriever.retrieve(sourceId, "harness substrate", 1);
        assertEquals(sourceId, result.sourceId());
        assertEquals("harness substrate", result.query());
        assertEquals(1, result.items().size());
        assertEquals(evidence.id(), result.items().iterator().next().id());
    }
}
