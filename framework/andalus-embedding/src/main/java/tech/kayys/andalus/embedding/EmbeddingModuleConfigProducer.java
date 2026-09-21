package tech.kayys.andalus.embedding;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Produces EmbeddingModuleConfig from MicroProfile config with fallback to defaults.
 */
@ApplicationScoped
public class EmbeddingModuleConfigProducer {

    @Produces
    @ApplicationScoped
    public EmbeddingModuleConfig produce() {
        EmbeddingModuleConfig config = new EmbeddingModuleConfig();
        Config mpConfig = ConfigProvider.getConfig();

        mpConfig.getOptionalValue("andalus.embedding.default-provider", String.class)
                .filter(value -> !value.isBlank())
                .ifPresent(config::setDefaultProvider);

        mpConfig.getOptionalValue("andalus.embedding.default-model", String.class)
                .filter(value -> !value.isBlank())
                .ifPresent(config::setDefaultModel);

        mpConfig.getOptionalValue("andalus.embedding.version", String.class)
                .filter(value -> !value.isBlank())
                .ifPresent(config::setEmbeddingVersion);

        mpConfig.getOptionalValue("andalus.embedding.normalize", Boolean.class)
                .ifPresent(config::setNormalize);

        mpConfig.getOptionalValue("andalus.embedding.cache.enabled", Boolean.class)
                .ifPresent(config::setCacheEnabled);

        mpConfig.getOptionalValue("andalus.embedding.cache.max-entries", Integer.class)
                .ifPresent(config::setCacheMaxEntries);

        mpConfig.getOptionalValue("andalus.embedding.batch.size", Integer.class)
                .ifPresent(config::setBatchSize);

        mpConfig.getOptionalValue("andalus.embedding.batch.queue-capacity", Integer.class)
                .ifPresent(config::setBatchQueueCapacity);

        mpConfig.getOptionalValue("andalus.embedding.batch.max-retries", Integer.class)
                .ifPresent(config::setBatchMaxRetries);

        mpConfig.getOptionalValue("andalus.embedding.batch.worker-threads", Integer.class)
                .ifPresent(config::setBatchWorkerThreads);

        mpConfig.getOptionalValue("andalus.embedding.tenant-strategies", String.class)
                .filter(value -> !value.isBlank())
                .ifPresent(config::loadTenantStrategies);

        return config;
    }
}
