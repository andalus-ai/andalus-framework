package tech.kayys.andalus.harness.memory.bridge;

import tech.kayys.andalus.harness.memory.MemoryContext;
import tech.kayys.andalus.harness.memory.MemoryEntry;
import tech.kayys.andalus.knowledge.KnowledgeContext;
import tech.kayys.andalus.knowledge.KnowledgeEvidence;

import java.util.concurrent.CompletionStage;

/**
 * Integration bridge allowing selected KnowledgeEvidence to be explicitly converted
 * into a canonical MemoryEntry candidate without conflating knowledge and memory.
 */
public interface KnowledgeMemoryBridge {

    CompletionStage<MemoryEntry> toMemoryCandidate(
            KnowledgeEvidence evidence,
            KnowledgeContext knowledgeContext,
            MemoryContext memoryContext
    );
}
