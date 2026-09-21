package tech.kayys.andalus.database;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;

import tech.kayys.andalus.configuration.ConfigurationResource;

/**
 * Database Configuration
 */
public record DatabaseConfig(
    String url,
    String username,
    String password,
    String driver,
    int maxPoolSize,
    int minIdle,
    long connectionTimeoutMs,
    long idleTimeoutMs,
    long maxLifetimeMs,
    String schema,
    boolean autoMigrate,
    List<String> migrationLocations
) {
    public static DatabaseConfig fromConfiguration(ConfigurationResource config) {
        return new DatabaseConfig(
            config.get("database.url", String.class),
            config.get("database.username", String.class),
            config.get("database.password", String.class),
            config.get("database.driver", String.class, "org.postgresql.Driver"),
            config.get("database.maxPoolSize", Integer.class, 10),
            config.get("database.minIdle", Integer.class, 2),
            config.get("database.connectionTimeout", Long.class, 30000L),
            config.get("database.idleTimeout", Long.class, 600000L),
            config.get("database.maxLifetime", Long.class, 1800000L),
            config.get("database.schema", String.class, "public"),
            config.get("database.autoMigrate", Boolean.class, true),
            config.get("database.migrationLocations", List.class, List.of("classpath:db/migration"))
        );
    }
}
