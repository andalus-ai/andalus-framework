package tech.kayys.andalus.harness.memory;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.memory.bridge.DefaultKnowledgeMemoryBridge;
import tech.kayys.andalus.knowledge.KnowledgeContext;
import tech.kayys.andalus.knowledge.KnowledgeEvidence;
import tech.kayys.andalus.knowledge.KnowledgeItem;
import tech.kayys.andalus.knowledge.KnowledgeValidity;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MemoryConsolidationPhase3Test {

    @Test
    void memoryContextCreationAndAttributes() {
        var ctx = MemoryContext.forExecution("exec-101", "agent-alpha", "session-42")
                .withAttribute("customKey", "customValue");

        assertEquals("exec-101", ctx.executionId());
        assertEquals("agent-alpha", ctx.agentId());
        assertEquals("session-42", ctx.sessionId());
        assertEquals("customValue", ctx.attributes().get("customKey"));
    }

    @Test
    void defaultAsyncMemoryStoreDelegatesSyncStore() throws Exception {
        var syncStore = new InMemoryMemoryStore();
        var asyncStore = new DefaultAsyncMemoryStore(syncStore);

        var entry = MemoryEntry.fact("The capital of Indonesia is Nusantara", MemoryScope.AGENT);
        MemoryId id = asyncStore.store(entry).toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertNotNull(id);
        Optional<MemoryEntry> retrieved = asyncStore.get(id).toCompletableFuture().get(5, TimeUnit.SECONDS);
        assertTrue(retrieved.isPresent());
        assertEquals("The capital of Indonesia is Nusantara", retrieved.get().content());

        var queryResult = asyncStore.query(MemoryQuery.of("Nusantara", MemoryScope.AGENT))
                .toCompletableFuture().get(5, TimeUnit.SECONDS);
        assertEquals(1, queryResult.entries().size());

        asyncStore.delete(id).toCompletableFuture().get(5, TimeUnit.SECONDS);
        assertTrue(asyncStore.get(id).toCompletableFuture().get(5, TimeUnit.SECONDS).isEmpty());
    }

    @Test
    void memoryRecordAdapterBiDirectional() {
        var entry = MemoryEntry.fact("User prefers dark mode", MemoryScope.SESSION);
        Map<String, Object> props = MemoryRecordAdapter.toRecordProperties(entry);

        assertEquals("User prefers dark mode", props.get("content"));
        assertEquals("fact", props.get("type"));
        assertEquals("SESSION", props.get("scope"));

        var recreated = MemoryRecordAdapter.fromRecord(
                (String) props.get("id"),
                (String) props.get("type"),
                (String) props.get("content"),
                (String) props.get("scope"),
                0.95,
                "ui-settings",
                Map.of("theme", "dark")
        );

        assertEquals(entry.id(), recreated.id());
        assertEquals(MemoryType.FACT, recreated.type());
        assertEquals(MemoryScope.SESSION, recreated.scope());
        assertEquals("User prefers dark mode", recreated.content());
    }

    @Test
    void memoryDefinitionAndPolicy() {
        var storeRef = MemoryStoreReference.of("in-memory-kv", "kv");
        var policy = MemoryPolicy.ephemeral(MemoryScope.SESSION);

        var definition = new MemoryDefinition(
                "session-cache",
                "1.0.0",
                "Session Cache",
                "Ephemeral session memory",
                MemoryScope.SESSION,
                List.of(MemoryType.FACT, MemoryType.PREFERENCE),
                storeRef,
                policy,
                List.of("memory.read", "memory.write"),
                Map.of("maxEntries", 1000),
                Map.of("tier", "hot")
        );

        assertEquals("session-cache", definition.id());
        assertEquals(MemoryScope.SESSION, definition.scope());
        assertTrue(definition.policy().allowAdmission());
        assertTrue(definition.policy().allowRetrieval());
        assertEquals(Duration.ofHours(1), definition.policy().retention());
    }

    @Test
    void defaultMemoryRuntimeRememberAndRetrieve() throws Exception {
        var syncStore = new InMemoryMemoryStore();
        var runtime = new DefaultMemoryRuntime(syncStore);
        var ctx = MemoryContext.forSession("sess-1", "agent-1");

        var entry = MemoryEntry.fact("Important customer preference", MemoryScope.SESSION);
        MemoryWriteResult writeResult = runtime.remember(entry, ctx).toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertEquals(WriteStatus.ADMITTED, writeResult.status());
        assertTrue(writeResult.memoryId().isPresent());

        var query = MemoryQuery.of("customer preference", MemoryScope.SESSION);
        var queryResult = runtime.retrieve(query, ctx).toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertEquals(1, queryResult.entries().size());
        assertEquals("Important customer preference", queryResult.entries().get(0).content());
    }

    @Test
    void defaultMemoryRuntimeAdmissionRejection() throws Exception {
        var syncStore = new InMemoryMemoryStore();
        MemoryAdmissionPolicy rejectAll = candidate -> false;
        var asyncStore = new DefaultAsyncMemoryStore(syncStore);
        var runtime = new DefaultMemoryRuntime(asyncStore, null, rejectAll, null);

        var entry = MemoryEntry.fact("Should not be admitted", MemoryScope.AGENT);
        MemoryWriteResult result = runtime.remember(entry, MemoryContext.empty()).toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertEquals(WriteStatus.REJECTED, result.status());
        assertTrue(result.memoryId().isEmpty());
    }

    @Test
    void memoryLifecycleMetadata() {
        var meta = MemoryLifecycleMetadata.active();
        assertEquals(MemoryLifecycleState.ACTIVE, meta.state());
        assertEquals(1L, meta.revision());

        var expired = meta.withExpired(Instant.now());
        assertEquals(MemoryLifecycleState.EXPIRED, expired.state());
        assertEquals(2L, expired.revision());

        var archived = meta.withArchived("Retention period reached");
        assertEquals(MemoryLifecycleState.ARCHIVED, archived.state());
        assertTrue(archived.archiveReason().isPresent());
        assertEquals("Retention period reached", archived.archiveReason().get());
    }

    @Test
    void knowledgeMemoryBridgePromotesEvidence() throws Exception {
        var bridge = new DefaultKnowledgeMemoryBridge();

        var item = new KnowledgeItem(
                "doc-42",
                "worldbank",
                "statistic",
                "GDP Growth",
                "Indonesia GDP growth forecast is 5.2% in 2026",
                Map.of("year", 2026),
                null,
                null,
                KnowledgeValidity.active()
        );

        var evidence = KnowledgeEvidence.of(item, 0.95, "vector-dense");
        var kCtx = new KnowledgeContext("tenant-x", "user-y", "agent-z", null, "economics", "finance",
                Instant.now(), List.of("worldbank"), Map.of());
        var mCtx = MemoryContext.forSession("sess-99", "agent-z");

        MemoryEntry candidate = bridge.toMemoryCandidate(evidence, kCtx, mCtx)
                .toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertNotNull(candidate);
        assertEquals("Indonesia GDP growth forecast is 5.2% in 2026", candidate.content());
        assertEquals(MemoryScope.SESSION, candidate.scope());
        assertEquals(0.95, candidate.metadata().confidence());
        assertEquals("knowledge", candidate.metadata().source().agentId());
        assertEquals("worldbank", candidate.metadata().source().executionId());
        assertEquals("doc-42", candidate.metadata().attributes().get("knowledge.itemId"));
    }
}
