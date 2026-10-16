package tech.kayys.andalus.harness.memory.bridge;

import tech.kayys.andalus.harness.memory.MemoryContext;
import tech.kayys.andalus.harness.memory.MemoryEntry;
import tech.kayys.andalus.harness.memory.MemoryId;
import tech.kayys.andalus.harness.memory.MemoryMetadata;
import tech.kayys.andalus.harness.memory.MemoryScope;
import tech.kayys.andalus.harness.memory.MemorySource;
import tech.kayys.andalus.harness.memory.MemoryType;
import tech.kayys.andalus.knowledge.KnowledgeContext;
import tech.kayys.andalus.knowledge.KnowledgeEvidence;
import tech.kayys.andalus.knowledge.KnowledgeItem;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Default implementation of KnowledgeMemoryBridge preserving provenance.
 */
public final class DefaultKnowledgeMemoryBridge implements KnowledgeMemoryBridge {

    @Override
    public CompletionStage<MemoryEntry> toMemoryCandidate(
            KnowledgeEvidence evidence,
            KnowledgeContext knowledgeContext,
            MemoryContext memoryContext
    ) {
        Objects.requireNonNull(evidence, "evidence must not be null");

        KnowledgeItem item = evidence.item();
        String content = item != null ? item.content() : "";
        String itemId = item != null ? item.id() : "unknown";
        String sourceId = item != null ? item.sourceId() : "knowledge";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("knowledge.itemId", itemId);
        attributes.put("knowledge.sourceId", sourceId);
        attributes.put("knowledge.retrievalMethod", evidence.retrievalMethod());
        attributes.put("knowledge.score", evidence.score());
        if (knowledgeContext != null) {
            attributes.put("knowledge.tenantId", knowledgeContext.tenantId());
            attributes.put("knowledge.domain", knowledgeContext.domain());
        }

        MemoryScope scope = memoryContext != null && memoryContext.sessionId() != null
                ? MemoryScope.SESSION
                : MemoryScope.AGENT;

        MemoryMetadata meta = new MemoryMetadata(
                Instant.now(),
                Instant.now(),
                new MemorySource("knowledge", sourceId, "knowledge", Instant.now()),
                evidence.score(),
                Map.copyOf(attributes)
        );

        MemoryEntry candidate = new MemoryEntry(
                MemoryId.generate(),
                MemoryType.FACT,
                content,
                scope,
                meta
        );

        return CompletableFuture.completedFuture(candidate);
    }
}
