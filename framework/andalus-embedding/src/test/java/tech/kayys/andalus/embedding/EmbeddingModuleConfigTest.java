package tech.kayys.andalus.embedding;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmbeddingModuleConfigTest {

    @AfterEach
    void clearSystemProperties() {
        System.clearProperty("andalus.embedding.default-provider");
        System.clearProperty("andalus.embedding.default-model");
        System.clearProperty("andalus.embedding.normalize");
        System.clearProperty("andalus.embedding.tenant-strategies");
        System.clearProperty("andalus.embedding.version");
        System.clearProperty("andalus.embedding.cache.enabled");
        System.clearProperty("andalus.embedding.cache.max-entries");
        System.clearProperty("andalus.embedding.batch.size");
        System.clearProperty("andalus.embedding.batch.queue-capacity");
        System.clearProperty("andalus.embedding.batch.max-retries");
        System.clearProperty("andalus.embedding.batch.worker-threads");
    }

    @Test
    void shouldLoadTenantStrategiesFromSpec() {
        EmbeddingModuleConfig config = new EmbeddingModuleConfig();
        config.loadTenantStrategies("tenant-a=tfidf:tfidf-512;tenant-b|hash|hash-384");

        var a = config.tenantStrategies().find("tenant-a");
        var b = config.tenantStrategies().find("tenant-b");

        assertTrue(a.isPresent());
        assertEquals("tfidf", a.get().provider());
        assertEquals("tfidf-512", a.get().model());

        assertTrue(b.isPresent());
        assertEquals("hash", b.get().provider());
        assertEquals("hash-384", b.get().model());
    }

    @Test
    void shouldRejectInvalidStrategyFormat() {
        EmbeddingModuleConfig config = new EmbeddingModuleConfig();
        assertThrows(IllegalArgumentException.class, () -> config.loadTenantStrategies("tenant-a=tfidf"));
    }

    @Test
    void shouldApplyOverridesFromSystemProperties() {
        System.setProperty("andalus.embedding.default-provider", "chargram");
        System.setProperty("andalus.embedding.default-model", "chargram-256");
        System.setProperty("andalus.embedding.normalize", "false");
        System.setProperty("andalus.embedding.tenant-strategies", "tenant-x=hash:hash-768");
        System.setProperty("andalus.embedding.version", "v2");
        System.setProperty("andalus.embedding.cache.enabled", "false");
        System.setProperty("andalus.embedding.cache.max-entries", "123");
        System.setProperty("andalus.embedding.batch.size", "7");
        System.setProperty("andalus.embedding.batch.queue-capacity", "33");
        System.setProperty("andalus.embedding.batch.max-retries", "5");
        System.setProperty("andalus.embedding.batch.worker-threads", "4");

        EmbeddingModuleConfig config = new EmbeddingModuleConfig();

        assertEquals("chargram", config.getDefaultProvider());
        assertEquals("chargram-256", config.getDefaultModel());
        assertTrue(!config.isNormalize());
        assertEquals("v2", config.getEmbeddingVersion());
        assertTrue(!config.isCacheEnabled());
        assertEquals(123, config.getCacheMaxEntries());
        assertEquals(7, config.getBatchSize());
        assertEquals(33, config.getBatchQueueCapacity());
        assertEquals(5, config.getBatchMaxRetries());
        assertEquals(4, config.getBatchWorkerThreads());
        var strategy = config.tenantStrategies().find("tenant-x");
        assertTrue(strategy.isPresent());
        assertEquals("hash", strategy.get().provider());
        assertEquals("hash-768", strategy.get().model());
    }
}
